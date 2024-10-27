# *Multi-threaded file sending through multiple ports using FileChannel and SocketChannel in Java NIO* 

## **Using**
Run file CreateFileExample.java để tạo thư mục server_storage và tạo file test_text.txt để làm mẫu gửi
\
**In example directory**:
- MultiFileChannelReadWithThreads.java 
    - Method readFilePart đọc file từ nhiều FileChannel mỗi channel sẽ được gắn một vị trí position nhất định trong file để đọc từ vị trí byte đó trở về phía sau
    - Method sendFilePart tương tự như readFilePart nhưng sau khi đọc xong nó sẽ gửi từng phần được đọc qua các port khác nhau đồng thời, các port phía nhận sẽ write tương úng vị trí vào file bên phía nhận
    - Hiện đã chỉnh thành chia ra x phần và y port, x phần này sẽ được chia đều cho y port (có thể 2 phần nhận chung 1 port (đã thêm xử lý đồng thời để tránh xung đột khi write vào cùng 1 port))
- FileReceiver.java thực hiện nhận kết quả từ nhiều port khác nhau
    - Hiện đang fix cứng từng port sẽ write tới từng phần cố định trong file
    - [Phát triển] Làm nó linh động hơn để ko bị fixed nữa. (Đã comment trong file)
\

**In example directory**:
- FileComparer.java để kiểm tra xem file có giống nhau hay không
