# Render View trong Spring MVC

Trong Spring MVC (sử dụng Spring Boot), có hai cách chính để render view HTML: tĩnh và động.

## 1. View HTML Tĩnh

View tĩnh là các file HTML, CSS, JavaScript không thay đổi. Spring Boot tự động phục vụ các file này từ thư mục `src/main/resources/static`.

**Cách hoạt động:**

1.  Đặt các file HTML của bạn vào thư mục `src/main/resources/static`.
    *   Ví dụ: `src/main/resources/static/my-static-page.html`
2.  Chạy ứng dụng Spring Boot.
3.  Truy cập vào URL tương ứng với đường dẫn file.
    *   Ví dụ: `http://localhost:8080/my-static-page.html`

Spring Boot sẽ tự động tìm và trả về file HTML tĩnh này.

## 2. View HTML Động (Sử dụng Template Engine)

View động cho phép bạn chèn dữ liệu từ backend (Java) vào file HTML trước khi gửi về cho trình duyệt. Để làm được điều này, bạn cần một *template engine*.

Dự án này hiện tại **chưa có** template engine nào được cấu hình trong `pom.xml`. Thymeleaf là một lựa chọn phổ biến và được tích hợp tốt với Spring Boot.

### Bước 1: Thêm dependency cho Template Engine (Ví dụ: Thymeleaf)

Để sử dụng Thymeleaf, bạn cần thêm `spring-boot-starter-thymeleaf` vào file `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

### Bước 2: Tạo file Template

Tạo file HTML template trong thư mục `src/main/resources/templates`. Spring Boot sẽ tự động tìm kiếm template ở đây.

*   Ví dụ: `src/main/resources/templates/user-profile.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>User Profile</title>
</head>
<body>
    <h1>Welcome, <span th:text="${username}">Guest</span>!</h1>
    <p>Your email is: <span th:text="${email}">N/A</span></p>
</body>
</html>
```

*   `th:text="${username}"`: Đây là cú pháp của Thymeleaf. Nó sẽ lấy giá trị của biến `username` từ model và hiển thị nó bên trong thẻ `<span>`.

### Bước 3: Tạo Controller để xử lý và trả về View

Trong Controller, bạn tạo một method trả về `String` là tên của file template (không bao gồm đuôi `.html`). Để truyền dữ liệu từ Controller sang View, bạn sử dụng đối tượng `Model`.

*   Ví dụ: `UserController.java`

```java
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @GetMapping("/profile")
    public String userProfile(@RequestParam(name="name", required=false, defaultValue="Guest") String name, Model model) {
        // 1. Thêm dữ liệu vào Model
        // Tên "username" trong model.addAttribute phải trùng với tên biến trong template (${username})
        model.addAttribute("username", name);
        model.addAttribute("email", name.toLowerCase() + "@example.com");

        // 2. Trả về tên của file template
        // Spring Boot sẽ tìm file /resources/templates/user-profile.html
        return "user-profile";
    }
}
```

**Cách hoạt động:**

1.  Khi người dùng truy cập `http://localhost:8080/profile?name=John`, `userProfile` method sẽ được gọi.
2.  Dữ liệu (`username` và `email`) được thêm vào đối tượng `Model`.
3.  Method trả về chuỗi `"user-profile"`.
4.  Spring Boot's `ViewResolver` (được tự động cấu hình cho Thymeleaf) sẽ tìm một file tên là `user-profile.html` trong thư mục `src/main/resources/templates`.
5.  Thymeleaf xử lý file template, thay thế các biểu thức `th:*` bằng dữ liệu từ `Model`.
6.  HTML cuối cùng được tạo ra và gửi về cho trình duyệt.

## Tóm tắt

| Loại View | Vị trí file | Cách hoạt động | Use case |
| :--- | :--- | :--- | :--- |
| **Tĩnh** | `src/main/resources/static/` | Truy cập trực tiếp qua URL. Server chỉ gửi file đi. | Trang giới thiệu, trang liên hệ, các file CSS/JS. |
| **Động** | `src/main/resources/templates/` | Controller trả về tên view + model. Template engine xử lý để tạo HTML. | Hiển thị thông tin người dùng, danh sách sản phẩm, kết quả tìm kiếm. |
