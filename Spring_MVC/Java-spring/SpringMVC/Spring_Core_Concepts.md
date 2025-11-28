# Beans vs Component

Trong Spring Framework, "Component" và "Bean" là hai khái niệm có liên quan chặt chẽ nhưng mang ý nghĩa khác nhau.

### Component

*   **Là gì:** Là một **class** được đánh dấu để Spring có thể tự động phát hiện và quản lý (thông qua cơ chế dependency injection).
*   **Sử dụng:** Bạn sử dụng một annotation, thường là `@Component`, trên một class do bạn viết. Annotation này hoạt động như một "biển chỉ dẫn" cho cơ chế quét (component scanning) của Spring.
*   **Ví dụ tương tự:** Hãy coi `@Component` như một **bản thiết kế**. Bạn đang nói với Spring rằng, "Đây là một class mà bạn nên biết và quản lý."

`@Controller`, `@Service`, và `@Repository` là các dạng chuyên biệt hóa của `@Component` cho các tầng cụ thể trong ứng dụng (web, logic nghiệp vụ, truy cập dữ liệu).

### Bean

*   **Là gì:** Là một **đối tượng (object)** được khởi tạo, lắp ráp và quản lý bởi Spring IoC (Inversion of Control) container.
*   **Tạo ra như thế nào:** Một bean là **thực thể (instance) được tạo ra từ một class Component** trong quá trình component scanning. Bean cũng có thể được khai báo một cách tường minh bằng cách sử dụng annotation `@Bean` bên trong một class có `@Configuration`.
*   **Ví dụ tương tự:** Bean chính là **đối tượng thực tế** được tạo ra từ bản thiết kế. Nó là thực thể "sống" bên trong Spring container và có thể được tiêm (inject) vào các đối tượng khác.

### Tóm tắt sự khác biệt chính

| Khía cạnh | Component | Bean |
| :--- | :--- | :--- |
| **Bản chất** | Là một **class** được đánh dấu để Spring quản lý. | Là một **đối tượng (object)** được Spring quản lý. |
| **Khai báo** | Dùng annotation ở cấp độ class (`@Component`, `@Service`, v.v.). | Là kết quả của component scanning hoặc dùng annotation `@Bean` ở cấp độ phương thức. |
| **Mức độ** | Là một kiểu (type) hoặc một "bản thiết kế". | Là một thực thể (instance) hoặc một "đối tượng". |

**Tóm lại:** Bạn đánh dấu một **class** là `@Component` để Spring có thể tạo ra một **Bean** (một đối tượng) từ class đó.

---

# Inversion of Control (IoC) và Dependency Injection (DI)

Đây là hai khái niệm nền tảng và quan trọng nhất trong Spring Framework.

### Inversion of Control (IoC) - Đảo ngược quyền điều khiển

**1. Luồng điều khiển truyền thống:**
*   Trong một ứng dụng thông thường, **chính bạn (lập trình viên) là người viết mã để điều khiển luồng của chương trình**.
*   Bạn quyết định khi nào tạo đối tượng (`new MyClass()`), khi nào gọi phương thức, và quản lý vòng đời của các đối tượng đó.
*   **Ví dụ:** Một lớp `Car` cần một đối tượng `Engine`. Trong luồng truyền thống, chính lớp `Car` sẽ tự tạo ra đối tượng `Engine` của riêng nó.

```java
public class Car {
    private Engine engine;

    public Car() {
        // Car tự mình tạo ra và kiểm soát đối tượng Engine
        this.engine = new Engine();
    }
}
```

**2. Luồng điều khiển bị đảo ngược (IoC):**
*   Với IoC, quyền kiểm soát này được **chuyển giao (đảo ngược)** cho một bên thứ ba - đó là một framework hoặc một container (trong trường hợp của Spring, đó là **Spring IoC Container**).
*   Framework sẽ chịu trách nhiệm khởi tạo đối tượng, liên kết các đối tượng với nhau (quản lý các dependency), và quản lý vòng đời của chúng.
*   Mã của bạn chỉ cần định nghĩa các "thành phần" và "quy tắc", sau đó "cắm" vào framework. Framework sẽ gọi đến mã của bạn khi cần thiết.

> **Tóm lại, IoC là một nguyên lý thiết kế.** Thay vì mã của bạn gọi đến các thư viện/framework, thì chính framework sẽ gọi đến mã của bạn. Quyền điều khiển luồng chương trình đã bị đảo ngược.

### Dependency Injection (DI) - Tiêm phụ thuộc

**Dependency Injection là một *kỹ thuật (pattern)* cụ thể để hiện thực hóa nguyên lý IoC.**

**1. Dependency (Sự phụ thuộc) là gì?**
*   Khi một lớp `A` cần sử dụng một phương thức hoặc thuộc tính của lớp `B`, ta nói `A` có một **sự phụ thuộc** vào `B`.

**2. Tiêm phụ thuộc (DI) hoạt động như thế nào?**
*   Thay vì một đối tượng tự tạo ra các phụ thuộc của nó (như ví dụ `Car` tự tạo `Engine` ở trên), các phụ thuộc này sẽ được **"tiêm" (inject)** vào đối tượng từ một nguồn bên ngoài (chính là Spring IoC Container).
*   **Ví dụ với DI:** Lớp `Car` không tự tạo `Engine`. Nó chỉ "khai báo" rằng nó *cần* một `Engine`. Spring Container sẽ tạo một `Engine` và "tiêm" nó vào cho `Car`.

```java
public class Car {
    private Engine engine;

    // Phụ thuộc được "tiêm" vào qua constructor
    public Car(Engine engine) {
        this.engine = engine;
    }
}
```

**Các hình thức "tiêm" phụ thuộc phổ biến trong Spring:**
1.  **Constructor Injection (Tiêm qua hàm khởi tạo):** Các phụ thuộc được cung cấp qua các tham số của constructor. Đây là cách được khuyến khích nhất.
2.  **Setter Injection (Tiêm qua hàm setter):** Các phụ thuộc được cung cấp qua các phương thức setter công khai.
3.  **Field Injection (Tiêm qua thuộc tính):** Các phụ thuộc được tiêm trực tiếp vào thuộc tính của lớp (sử dụng `@Autowired`).

### Mối quan hệ giữa IoC và DI

*   **IoC** là **nguyên lý** (cái gì - "what"): "Hãy đảo ngược quyền kiểm soát, giao nó cho container."
*   **DI** là **cách thực hiện** (làm thế nào - "how"): "Làm sao để giao quyền? Bằng cách 'tiêm' các phụ thuộc từ bên ngoài vào."

> Nói cách khác: Bạn đạt được **Inversion of Control** bằng cách sử dụng kỹ thuật **Dependency Injection**.

---

# Tightly Coupled vs Loosely Coupled

Đây là một khái niệm cốt lõi trong thiết kế phần mềm, và là lý do chính mà các framework như Spring tồn tại.

### Tightly Coupled - Liên kết chặt chẽ

*   **Định nghĩa:** Là tình trạng các lớp hoặc thành phần trong hệ thống phụ thuộc quá nhiều vào chi tiết triển khai của nhau. Một lớp `A` thường tự mình khởi tạo một đối tượng cụ thể của `B`.
*   **Ví dụ:**
    ```java
    public class Car {
        private PetrolEngine engine;

        public Car() {
            // ==> Liên kết chặt chẽ xảy ra ở đây <==
            // Lớp Car trực tiếp tạo và phụ thuộc vào lớp *cụ thể* là PetrolEngine.
            this.engine = new PetrolEngine(); 
        }
    }
    ```
*   **Nhược điểm:**
    *   **Khó bảo trì và thay đổi:** Muốn đổi sang `ElectricEngine`, bạn phải sửa code bên trong lớp `Car`.
    *   **Khó kiểm thử (Unit Test):** Không thể tách rời `Car` khỏi `PetrolEngine` để kiểm thử riêng lẻ.
    *   **Giảm khả năng tái sử dụng:** Lớp `Car` bị "hàn chết" với `PetrolEngine`.

### Loosely Coupled - Liên kết lỏng lẻo

*   **Định nghĩa:** Là trạng thái mà các thành phần ít phụ thuộc vào nhau. Chúng giao tiếp với nhau thông qua các **giao diện (interfaces)**, thay vì các lớp cụ thể.
*   **Ví dụ (sử dụng Dependency Injection):**
    ```java
    // Lớp Car giờ đây phụ thuộc vào Interface, không phải lớp cụ thể
    public class Car {
        private Engine engine; // Phụ thuộc vào interface Engine

        // Phụ thuộc được "tiêm" vào từ bên ngoài.
        // Car không quan tâm engine là loại gì, chỉ cần nó là một Engine.
        public Car(Engine engine) {
            this.engine = engine;
        }
    }
    ```
    Việc tạo ra chiếc xe nào là do **bên ngoài** quyết định:
    ```java
    Car petrolCar = new Car(new PetrolEngine()); // Tạo xe chạy xăng
    Car electricCar = new Car(new ElectricEngine()); // Tạo xe chạy điện
    ```
*   **Ưu điểm:**
    *   **Dễ bảo trì và mở rộng:** Dễ dàng thay đổi các loại `Engine` mà không cần sửa lớp `Car`.
    *   **Dễ kiểm thử:** Có thể "giả lập" (mock) một `Engine` để kiểm thử `Car`.
    *   **Tăng khả năng tái sử dụng:** Lớp `Car` có thể hoạt động với bất kỳ loại `Engine` nào.

### Kết luận

> **Spring Framework, thông qua Inversion of Control (IoC) và Dependency Injection (DI), giúp chúng ta xây dựng các ứng dụng có tính liên kết lỏng lẻo (Loosely Coupled),** giúp code sạch sẽ, linh hoạt và dễ bảo trì hơn.

---

# Injector - Trình tiêm phụ thuộc

*   **Injector là gì?**
    *   Injector là một đối tượng hoặc một thành phần chịu trách nhiệm **thực hiện hành động "tiêm"** các phụ thuộc.
    *   Nó là **cơ chế thực thi** cho mẫu thiết kế Dependency Injection.
    *   Trong Spring Framework, vai trò của Injector được đảm nhiệm bởi **Spring IoC Container** (mà đại diện cụ thể thường là `ApplicationContext`).

*   **Nhiệm vụ của Injector:**
    1.  **Đọc cấu hình:** Quét các lớp và các annotation (`@Component`, `@Autowired`, v.v.).
    2.  **Tạo đối tượng phụ thuộc:** Tạo ra các bean (ví dụ: `PetrolEngine`) và lưu vào "kho" (container).
    3.  **Tạo đối tượng chính và tiêm phụ thuộc:** Khi tạo một bean (ví dụ: `Car`), nó tìm các phụ thuộc cần thiết (ví dụ: `Engine`) trong "kho" và "tiêm" chúng vào.

> **Tóm lại:** Injector là "người trung gian" đọc yêu cầu của bạn, tự động tìm kiếm, tạo ra, rồi cung cấp (tiêm) đối tượng phù hợp cho bạn.

### Mối quan hệ với các khái niệm khác

*   **IoC (Đảo ngược quyền điều khiển):** Là nguyên lý.
*   **DI (Tiêm phụ thuộc):** Là kỹ thuật để thực hiện nguyên lý IoC.
*   **Injector (Trình tiêm phụ thuộc):** Là công cụ/cơ chế để thực hiện kỹ thuật DI.
