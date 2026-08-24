import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SimpleHttpServer {
    public static void main(String[] args) throws Exception {
        int port = 8000;
        String assetsPath = "C:\\Users\\Administrator\\Music\\online_assets";
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new FileHandler(assetsPath));
        server.setExecutor(null); // default executor
        System.out.println("Java HTTP Asset Server started on port " + port);
        System.out.println("Serving files from: " + assetsPath);
        server.start();
    }

    static class FileHandler implements HttpHandler {
        private final String baseDir;

        public FileHandler(String baseDir) {
            this.baseDir = baseDir;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            // Prevent directory traversal
            path = path.replace("..", "");
            File file = new File(baseDir, path);

            if (file.isDirectory()) {
                File index = new File(file, "index.html");
                if (index.exists()) {
                    file = index;
                } else {
                    sendError(exchange, 403, "Directory browsing is disabled");
                    return;
                }
            }

            if (!file.exists()) {
                sendError(exchange, 404, "File Not Found: " + path);
                return;
            }

            // Determine content type
            String contentType = "application/octet-stream";
            if (file.getName().endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (file.getName().endsWith(".mp3")) {
                contentType = "audio/mpeg";
            }

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, file.length());

            OutputStream os = exchange.getResponseBody();
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int count;
                while ((count = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, count);
                }
            } finally {
                os.close();
            }
        }

        private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
            byte[] response = message.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(statusCode, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
    }
}
