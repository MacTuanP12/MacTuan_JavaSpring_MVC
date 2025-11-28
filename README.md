# 🍃 Tổng Quan Về Spring MVC

**Spring MVC** (Model-View-Controller) là một module trong Spring Framework, cung cấp kiến trúc để xây dựng các ứng dụng web linh hoạt và lỏng lẻo (loosely coupled).

---

## 1. 🏗️ Kiến trúc & Luồng hoạt động (Workflow)

Trái tim của Spring MVC là **DispatcherServlet** (Front Controller). Nó đóng vai trò là "người điều phối" trung tâm, nhận mọi request và phân phối đến các thành phần xử lý khác.

[Image of Spring MVC request execution flow]

### Chi tiết các bước xử lý Request:

1.  **Client gửi Request:** Người dùng gửi yêu cầu (HTTP Request) đến server.
2.  **DispatcherServlet nhận tin:** Request đến `DispatcherServlet` đầu tiên.
3.  **Hỏi đường (HandlerMapping):** `DispatcherServlet` hỏi `HandlerMapping` xem request này (URL này) do Controller nào xử lý.
4.  **Xử lý (Controller):** `DispatcherServlet` chuyển request đến `Controller` tương ứng. Controller gọi xuống Service/DAO để xử lý logic nghiệp vụ.
5.  **Trả về Model & View:** Controller xử lý xong, trả về tên của View (logic view name) và dữ liệu (Model) cho `DispatcherServlet`.
6.  **Định vị View (ViewResolver):** `DispatcherServlet` hỏi `ViewResolver` để tìm file giao diện thật sự (ví dụ: `home.jsp`, `index.html`) dựa trên tên view nhận được.
7.  **Render View:** View được ghép với dữ liệu (Model) và render thành HTML.
8.  **Phản hồi:** `DispatcherServlet` trả HTTP Response về cho người dùng.

> **Lưu ý:** Trong các ứng dụng hiện đại (như JHipster/React), bước 6 & 7 thường được bỏ qua. Thay vào đó, Controller dùng `@RestController` để trả về **JSON** trực tiếp cho Frontend (React/Angular).

---

## 2. 🧩 Các thành phần cốt lõi

| Thành phần            | Vai trò                                                     |
| :-------------------- | :---------------------------------------------------------- |
| **DispatcherServlet** | Front Controller, cửa ngõ đón nhận mọi request.             |
| **HandlerMapping**    | "Bản đồ" ánh xạ giữa URL và Method trong Controller.        |
| **Controller**        | Nơi chứa logic điều hướng, nhận input và gọi Service xử lý. |
| **ViewResolver**      | Giúp tìm ra file view (JSP, Thymeleaf) từ tên view logic.   |
| **Model**             | Dùng để chứa dữ liệu truyền từ Controller sang View.        |

---

## 3. 🏷️ Các Annotation thường dùng (Cheat Sheet)

Đây là phần quan trọng nhất khi code hàng ngày.

### Khai báo Controller

- `@Controller`: Đánh dấu class là một Controller (thường dùng cho web trả về HTML/JSP).
- `@RestController`: Sự kết hợp của `@Controller` + `@ResponseBody`. Dùng cho **RESTful API** (trả về JSON/XML).

### Ánh xạ Request (Mapping)

- `@RequestMapping`: Annotation gốc, map URL vào method (hoặc class).
- `@GetMapping("/path")`: Xử lý HTTP GET.
- `@PostMapping("/path")`: Xử lý HTTP POST (tạo mới).
- `@PutMapping("/path")`: Xử lý HTTP PUT (cập nhật).
- `@DeleteMapping("/path")`: Xử lý HTTP DELETE.

### Nhận dữ liệu từ Client

- `@RequestParam`: Lấy tham số từ Query String (VD: `?name=Tuan` -> `@RequestParam String name`).
- `@PathVariable`: Lấy tham số từ đường dẫn (VD: `/users/1` -> `@PathVariable Long id`).
- `@RequestBody`: Map JSON từ body request vào Java Object (Dùng cho POST/PUT).
- `@ModelAttribute`: Bind dữ liệu từ Form HTML vào Java Object.

---

## 4. 💻 Ví dụ Code thực tế

### A. Mô hình MVC truyền thống (Server-side Rendering)

_Dùng cho các trang quản trị cũ hoặc Thymeleaf/JSP._

```java
@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // GET: /users/profile?id=1
    @GetMapping("/profile")
    public String getUserProfile(@RequestParam("id") Long userId, Model model) {
        User user = userService.findById(userId);

        // Đẩy dữ liệu sang View
        model.addAttribute("userInfo", user);

        // Trả về tên file view (VD: profile.html)
        return "profile";
    }
}
```
