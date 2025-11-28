# Tổng quan về Spring Security

**Spring Security** là một framework mạnh mẽ, cung cấp các giải pháp bảo mật toàn diện cho ứng dụng Java, đặc biệt là các ứng dụng Spring. Nó giống như một **đội ngũ bảo vệ chuyên nghiệp**, xử lý cả hai nhiệm vụ chính:

1.  **Authentication (Xác thực):** Kiểm tra xem **bạn là ai** (ví dụ: kiểm tra username và password khi đăng nhập).
2.  **Authorization (Ủy quyền):** Sau khi đã xác thực, kiểm tra xem **bạn được phép làm gì** (ví dụ: chỉ `ADMIN` mới được vào trang quản trị).

### Kiến trúc cốt lõi: Chuỗi Filter (Filter Chain)

Mọi request gửi đến ứng dụng đều phải đi qua một chuỗi các "trạm kiểm soát" (Filter) của Spring Security **trước khi** đến được Controller của bạn. Chuỗi này xử lý mọi thứ từ xác thực, ủy quyền, chống tấn công CSRF, quản lý session,...

### Các thành phần chính cần nhớ:

*   **`SecurityFilterChain`**: Định nghĩa các quy tắc bảo mật (URL nào cần đăng nhập, URL nào công khai).
*   **`AuthenticationManager`**: "Giám đốc an ninh", điều phối quá trình xác thực.
*   **`UserDetailsService`**: Dịch vụ dùng để tải thông tin người dùng (từ database, bộ nhớ,...).
*   **`PasswordEncoder`**: Dịch vụ dùng để mã hóa và so sánh mật khẩu.
*   **`SecurityContextHolder`**: Nơi lưu trữ thông tin của người dùng đã được xác thực trong suốt phiên làm việc.

---

# Spring Security và Spring MVC hoạt động cùng nhau như thế nào?

> **Triết lý cốt lõi:** Spring Security là một lớp bảo vệ nằm **phía trước** Spring MVC. Mọi request phải đi qua "đội bảo vệ" (Spring Security) trước khi được "người phục vụ" (Spring MVC) xử lý.

### Luồng hoạt động chi tiết

1.  **Request đến:** Người dùng gửi một request (ví dụ: truy cập `/admin/dashboard`).
2.  **Spring Security Intercepts (Bảo vệ chặn lại):** Request này bị **`FilterChainProxy`** của Spring Security chặn lại đầu tiên. Nó **chưa** đến được `DispatcherServlet` của MVC.
3.  **Authentication Check (Kiểm tra xác thực):**
    *   Một filter trong chuỗi sẽ kiểm tra xem người dùng đã đăng nhập trong phiên làm việc này chưa (thông qua `SecurityContextHolder`).
    *   Nếu chưa đăng nhập, người dùng sẽ bị chuyển hướng đến trang login.
    *   Nếu đã đăng nhập, thông tin `Authentication` (chứa username, vai trò) của họ được tải lên và request được đi tiếp.
4.  **Authorization Check (Kiểm tra ủy quyền):**
    *   Request đi đến `AuthorizationFilter`. Filter này nhìn vào URL và đối chiếu với cấu hình bảo mật bạn đã viết (ví dụ: `.requestMatchers("/admin/**").hasRole("ADMIN")`).
    *   Nó kiểm tra xem người dùng có vai trò (`Role`) cần thiết không.
    *   **Thành công:** Nếu người dùng có quyền, request được đi tiếp.
    *   **Thất bại:** Nếu không có quyền, chuỗi filter sẽ dừng lại và trả về lỗi **403 Forbidden (Từ chối truy cập)**. Request này **sẽ không bao giờ đến được Controller**.
5.  **Handover to MVC (Bàn giao cho MVC):**
    *   Sau khi vượt qua tất cả các chốt kiểm tra an ninh, request cuối cùng cũng được chuyển đến **`DispatcherServlet`** của Spring MVC.
6.  **Normal MVC Flow (Luồng MVC bình thường):**
    *   Từ đây, `DispatcherServlet` sẽ điều phối request đến `Controller`, `Model` và `View` như bình thường để xử lý logic nghiệp vụ và tạo ra response.
7.  **Response Goes Back (Phản hồi quay trở lại):**
    *   Response (trang HTML) được tạo ra sẽ đi ngược lại qua chuỗi filter của Spring Security trước khi gửi về trình duyệt. Các filter có thể thêm các header bảo mật vào response tại bước này.

---

# Thứ tự các Filter quan trọng trong Spring Security

Thứ tự của các Filter là **cực kỳ quan trọng** vì chúng hoạt động như một chuỗi các chốt kiểm tra an ninh. Dưới đây là thứ tự logic của một số Filter quan trọng nhất, từ đầu đến cuối chuỗi:

1.  **`SecurityContextPersistenceFilter`**
    *   **Nhiệm vụ:** Chạy rất sớm, đọc `HttpSession` để xem người dùng đã đăng nhập từ các request trước đó chưa. Nếu có, nó sẽ lấy thông tin (`SecurityContext`) và đưa vào `SecurityContextHolder` để các filter sau có thể sử dụng.
    *   **Tương tự:** *Nhân viên kiểm tra xem bạn đã check-in online chưa để lấy thông tin vé của bạn ra ngay lập tức.*

2.  **`LogoutFilter`**
    *   **Nhiệm vụ:** Chuyên theo dõi URL đăng xuất (mặc định là `/logout`). Nếu khớp, nó sẽ xóa thông tin đăng nhập, vô hiệu hóa session và chuyển hướng. Request sẽ dừng lại ở đây.
    *   **Tương tự:** *Một lối ra đặc biệt. Nếu bạn đi vào lối này, bạn sẽ được đưa ra khỏi sân bay ngay lập tức.*

3.  **`CsrfFilter`**
    *   **Nhiệm vụ:** Bảo vệ chống lại các cuộc tấn công CSRF bằng cách kiểm tra một token bí mật trong các request `POST`, `PUT`, `DELETE`.
    *   **Tương tự:** *Chốt kiểm tra hành lý ký gửi, đảm bảo không có vật phẩm cấm nào được đưa lên máy bay.*

4.  **`UsernamePasswordAuthenticationFilter`**
    *   **Nhiệm vụ:** Xử lý việc đăng nhập bằng form. Nó chỉ hoạt động trên URL đăng nhập (mặc định là `POST /login`), trích xuất username/password và yêu cầu `AuthenticationManager` xác thực.
    *   **Tương tự:** *Quầy check-in, nơi bạn xuất trình giấy tờ tùy thân để nhận được thẻ lên máy bay.*

5.  **`AuthorizationFilter`** (hay `FilterSecurityInterceptor`)
    *   **Nhiệm vụ:** Là một trong những filter cuối cùng, đóng vai trò người gác cổng cuối cùng. Nó quyết định xem người dùng đã được xác thực có đủ quyền (vai trò) để truy cập vào URL được yêu cầu hay không.
    *   **Tương tự:** *Nhân viên ở cửa khởi hành (gate), người quét thẻ lên máy bay của bạn để xác nhận rằng bạn được phép lên đúng chuyến bay này.*

**Làm thế nào để xem thứ tự Filter?**
Thêm dòng sau vào tệp `application.properties` và khởi động lại ứng dụng. Console sẽ in ra toàn bộ chuỗi filter theo đúng thứ tự.
```properties
logging.level.org.springframework.security=DEBUG
```

---

# Authentication vs Authorization

Đây là hai khái niệm cực kỳ quan trọng trong lĩnh vực bảo mật phần mềm, và thường bị nhầm lẫn với nhau.

**Nói một cách đơn giản nhất:**
*   **Authentication (Xác thực)** là quá trình xác minh **bạn là ai**.
*   **Authorization (Ủy quyền)** là quá trình xác định **bạn được phép làm gì**.

---

### Authentication (Xác thực)

1.  **Câu hỏi cốt lõi:** "Who are you?" (Bạn là ai?)
2.  **Mục đích:** Để chứng minh danh tính của một người dùng. Hệ thống cần chắc chắn rằng người dùng đúng là người mà họ tự nhận.
3.  **Cơ chế hoạt động:** Người dùng phải cung cấp "bằng chứng" để chứng minh danh tính. Bằng chứng này thường thuộc một trong ba loại:
    *   **Thứ bạn biết:** Mật khẩu, mã PIN.
    *   **Thứ bạn có:** Điện thoại (để nhận mã OTP), khóa bảo mật vật lý.
    *   **Thứ bạn là:** Dấu vân tay, khuôn mặt (sinh trắc học).
4.  **Kết quả:** Là một câu trả lời Có/Không. Hoặc là bạn được xác thực thành công (đăng nhập thành công) hoặc là không.
5.  **Ví dụ tương tự:** Khi bạn đến quầy check-in ở sân bay, bạn phải xuất trình Căn cước công dân hoặc Hộ chiếu. Nhân viên sân bay sẽ **xác thực** danh tính của bạn. Họ đang trả lời câu hỏi "Bạn có đúng là người này không?".

### Authorization (Ủy quyền)

1.  **Câu hỏi cốt lõi:** "What are you allowed to do?" (Bạn được phép làm gì?)
2.  **Mục đích:** Để kiểm tra xem một người dùng **đã được xác thực** có quyền truy cập vào một tài nguyên hoặc thực hiện một hành động cụ thể hay không.
3.  **Điều kiện tiên quyết:** Authorization luôn luôn xảy ra **sau khi** Authentication thành công. Hệ thống không thể cấp quyền cho một người mà nó không biết là ai.
4.  **Cơ chế hoạt động:** Dựa trên vai trò (roles) hoặc quyền hạn (permissions) đã được gán cho người dùng.
    *   **Role-Based Access Control (RBAC):** Người dùng được gán các vai trò như `ADMIN`, `USER`, `GUEST`. Mỗi vai trò sẽ có một bộ quyền hạn nhất định.
    *   **Permission-Based:** Quyền hạn được gán trực tiếp cho người dùng, ví dụ: `read_profile`, `write_post`.
5.  **Ví dụ tương tự:** Sau khi bạn đã check-in thành công (đã được xác thực), bạn sẽ nhận được một chiếc **thẻ lên máy bay (Boarding Pass)**. Chiếc thẻ này chính là sự **ủy quyền**. Nó cho phép bạn đi qua cửa an ninh, lên đúng chuyến bay, và ngồi đúng số ghế. Nó **không** cho phép bạn vào buồng lái của phi công.

### Bảng tóm tắt

| Khía cạnh | Authentication (Xác thực) | Authorization (Ủy quyền) |
| :--- | :--- | :--- |
| **Mục đích** | Xác minh danh tính (bạn là ai). | Xác định quyền truy cập (bạn được làm gì). |
| **Thứ tự** | Xảy ra **trước**. | Xảy ra **sau** khi xác thực thành công. |
| **Đầu vào** | Thông tin đăng nhập (username/password, OTP...). | Vai trò, quyền hạn của người dùng. |
| **Kết quả** | Đăng nhập thành công / thất bại. | Được phép truy cập / Bị từ chối (Access Denied). |
| **Ví dụ** | Đăng nhập vào một trang web. | Sau khi đăng nhập, chỉ admin mới thấy nút "Quản lý người dùng". |

Trong Spring, **Spring Security** là một framework mạnh mẽ giúp bạn xử lý cả hai quá trình này một cách hiệu quả.

---

# Các Nguyên Tắc Thiết Kế Bảo Mật Cho Hệ Thống Web

Triết lý chung và quan trọng nhất là **"Defense in Depth" (Bảo mật theo chiều sâu)**. Chúng ta cần tạo ra nhiều lớp bảo vệ khác nhau, thay vì chỉ dựa vào một cơ chế duy nhất.

### 1. Never Trust User Input (Không bao giờ tin tưởng dữ liệu từ người dùng)
*   **Ý nghĩa:** Đây là nguyên tắc vàng. Mọi dữ liệu đến từ phía client đều có thể chứa mã độc.
*   **Hành động:**
    *   **Validation (Kiểm tra tính hợp lệ):** Luôn kiểm tra dữ liệu đầu vào (ví dụ: email phải đúng định dạng).
    *   **Sanitization (Làm sạch):** Loại bỏ các ký tự nguy hiểm.
    *   **Escaping (Thoát ký tự):** Vô hiệu hóa các thẻ HTML/script khi hiển thị lại dữ liệu để chống **Cross-Site Scripting (XSS)**.
    *   **Chống SQL Injection:** Luôn dùng Prepared Statements hoặc ORM (JPA/Hibernate).

### 2. Principle of Least Privilege (Nguyên tắc Đặc quyền Tối thiểu)
*   **Ý nghĩa:** Mỗi người dùng hoặc thành phần hệ thống chỉ nên được cấp những quyền hạn **tối thiểu và cần thiết nhất** để hoàn thành công việc.
*   **Ví dụ:** Một người dùng thông thường không có quyền truy cập vào trang quản trị (`/admin`).

### 3. Fail-Secure (An toàn khi gặp lỗi)
*   **Ý nghĩa:** Khi một chức năng nào đó gặp lỗi, hệ thống phải chuyển về trạng thái an toàn nhất, tức là **từ chối truy cập**.
*   **Ví dụ:** Nếu không thể kiểm tra quyền của người dùng, hệ thống phải chặn yêu cầu đó lại, chứ không được mặc định cho qua.

### 4. Secure by Default (Mặc định an toàn)
*   **Ý nghĩa:** Cấu hình mặc định của hệ thống phải là cấu hình an toàn nhất.
*   **Ví dụ:** Spring Security mặc định chặn tất cả các request. Bạn phải tự tay cấu hình để cho phép truy cập vào những đường dẫn cụ thể.

### 5. Don't Reinvent the Wheel (Không tự sáng chế lại bánh xe)
*   **Ý nghĩa:** Đừng bao giờ tự viết các thuật toán bảo mật của riêng mình, đặc biệt là mã hóa.
*   **Hành động:** Hãy sử dụng các thư viện, framework đã được cộng đồng kiểm chứng (như Spring Security).
*   **Ví dụ:** Dùng thuật toán **BCrypt** (có sẵn trong Spring Security) để băm mật khẩu, không lưu mật khẩu dạng plain text.

### 6. Separation of Concerns (Phân tách các mối quan tâm)
*   **Ý nghĩa:** Tách biệt logic nghiệp vụ khỏi logic bảo mật để code dễ đọc và bảo trì.
*   **Ví dụ:** Spring Security sử dụng một chuỗi các Filter để xử lý bảo mật **trước khi** request đến được `Controller` của bạn.

### 7. Keep Security Simple (Giữ cho bảo mật đơn giản)
*   **Ý nghĩa:** Một hệ thống bảo mật quá phức tạp sẽ khó hiểu, khó kiểm thử và dễ chứa lỗ hổng.
*   **Hành động:** Ưu tiên các giải pháp đơn giản và rõ ràng.

### 8. Logging and Monitoring (Ghi log và Giám sát)
*   **Ý nghĩa:** Bạn không thể chống lại thứ mà bạn không nhìn thấy. Ghi log giúp phát hiện hành vi bất thường và điều tra khi có sự cố.
*   **Ví dụ:** Ghi lại các lần đăng nhập thất bại, các lần truy cập vào tài nguyên nhạy cảm.

---

# Hướng Dẫn Cài Đặt Spring Security (Cơ bản)

Việc thiết lập Spring Security bao gồm 3 bước chính:

### Bước 1: Thêm Dependency vào `pom.xml`
Thêm `spring-boot-starter-security` vào tệp `pom.xml`. Starter này sẽ tự động kích hoạt Spring Security với cấu hình mặc định (yêu cầu đăng nhập cho mọi trang).

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### Bước 2: Tạo Lớp Cấu Hình Bảo Mật
Tạo một lớp Java để định nghĩa các quy tắc bảo mật của riêng bạn. Đây là cách cấu hình hiện đại (từ Spring Boot 3).

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Cho phép tất cả mọi người truy cập vào các đường dẫn này
                .requestMatchers("/", "/home", "/public/**", "/css/**", "/js/**").permitAll() 
                // Tất cả các request khác đều cần phải được xác thực (phải đăng nhập)
                .anyRequest().authenticated() 
            )
            .formLogin(formLogin -> formLogin
                // Đường dẫn đến trang đăng nhập của bạn (nếu có)
                .loginPage("/login") 
                // Cho phép tất cả mọi người truy cập vào trang đăng nhập
                .permitAll() 
            )
            .logout(logout -> logout
                // Đường dẫn để kích hoạt logout
                .logoutUrl("/logout") 
                .permitAll() 
            );

        return http.build();
    }
}
```

### Bước 3: Cung Cấp Thông Tin Người Dùng và Mã Hóa Mật Khẩu
Bạn phải cung cấp cho Spring Security một `UserDetailsService` để tìm thông tin người dùng và một `PasswordEncoder` để mã hóa mật khẩu.

Cập nhật lại lớp `SecurityConfig.java`:

```java
// ... các import cần thiết
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // (Bean SecurityFilterChain từ Bước 2 giữ nguyên ở đây)
    // ...

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Sử dụng BCrypt để mã hóa mật khẩu
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Tạo người dùng trong bộ nhớ (chỉ dành cho demo)
        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("password")) // Mật khẩu "password" đã được mã hóa
            .roles("USER")
            .build();

        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin123"))
            .roles("ADMIN", "USER")
            .build();

        // Quản lý người dùng trong bộ nhớ
        return new InMemoryUserDetailsManager(user, admin);
    }
}
```
**Lưu ý:** Trong ứng dụng thực tế, bạn sẽ thay thế `InMemoryUserDetailsManager` bằng một service đọc thông tin người dùng từ database.
