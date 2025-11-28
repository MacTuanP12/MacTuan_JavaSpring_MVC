# Tóm Tắt Về MVC và Spring MVC

## 1. Mô Hình MVC (Tóm Tắt)

**MVC (Model-View-Controller)** là mô hình kiến trúc nhằm **tách biệt** logic nghiệp vụ, dữ liệu và giao diện người dùng.

Hãy tưởng tượng nó như một nhà hàng:
*   **Model:** Chứa **dữ liệu** và **logic xử lý**. (Giống như *nhà bếp*).
*   **View:** Chịu trách nhiệm **hiển thị** dữ liệu cho người dùng. (Giống như *món ăn được trình bày*).
*   **Controller:** **Nhận yêu cầu** từ người dùng và **điều phối** giữa Model và View. (Giống như *người phục vụ*).

## 2. Spring MVC (Tóm Tắt)

**Spring MVC** là một module của Spring Framework giúp xây dựng ứng dụng web theo mô hình MVC.

"Trái tim" của nó là một `Controller` trung tâm gọi là **`DispatcherServlet`**, đóng vai trò như một "tổng quản lý" điều phối mọi yêu cầu.

### Luồng Hoạt Động Chính

1.  Mọi request của người dùng đều đi đến **`DispatcherServlet`** đầu tiên.
2.  `DispatcherServlet` dùng **`HandlerMapping`** để tìm ra `@Controller` nào sẽ xử lý request này.
3.  `@Controller` thực thi logic, làm việc với **Model** (các lớp Service, Repository) để lấy dữ liệu, sau đó trả về **tên của View** và **dữ liệu (Model)**.
4.  `DispatcherServlet` đưa tên View cho **`ViewResolver`** để tìm file template tương ứng (ví dụ: `home.html`).
5.  **View** (file template) nhận dữ liệu và render ra trang HTML cuối cùng để trả về cho người dùng.

---

# Phụ lục: Java Annotation là gì?

**Annotation (Chú thích)** là một khái niệm nền tảng giúp Spring và nhiều framework khác hoạt động.

### 1. Annotation là gì?

**Annotation** là một dạng **siêu dữ liệu (metadata)** mà bạn có thể thêm vào mã nguồn Java. Hãy nghĩ nó như những **"nhãn dán"** hoặc **"thẻ tag"** mà bạn có thể dán lên các phần của code (lớp, phương thức, biến,...).

Bản thân annotation **không làm thay đổi** logic của chương trình. Tuy nhiên, các công cụ khác (như trình biên dịch hoặc các framework như Spring) có thể **đọc** những "nhãn dán" này và thực hiện một hành động nào đó dựa trên thông tin của chúng.

### 2. Tại sao phải sử dụng Annotation?

1.  **Cung cấp thông tin cho trình biên dịch:** Giúp phát hiện lỗi. Ví dụ: `@Override`, `@Deprecated`.
2.  **Xử lý tại thời điểm build:** Các công cụ có thể đọc annotation để tự động sinh ra mã nguồn. Ví dụ: **Lombok** dùng `@Getter`, `@Setter` để tự động sinh ra các phương thức tương ứng.
3.  **Xử lý tại thời điểm chạy (Runtime):** Đây là mục đích sử dụng mạnh mẽ nhất trong các framework. Framework sẽ quét code của bạn lúc khởi động, tìm các annotation và sử dụng chúng để "cấu hình" ứng dụng một cách tự động.

### 3. Ví dụ về Annotation trong Spring MVC

*   `@Controller`: "Dán nhãn" một lớp là một Controller, để `HandlerMapping` có thể tìm thấy nó.
*   `@RequestMapping`, `@GetMapping`: "Dán nhãn" một phương thức, ánh xạ nó với một URL cụ thể.
*   `@Autowired`: "Dán nhãn" một thuộc tính, yêu cầu Spring tự động tìm và "tiêm" một bean phù hợp vào đó.
*   `@Service`, `@Repository`: "Dán nhãn" các lớp là thành phần logic nghiệp vụ hoặc truy cập dữ liệu.


