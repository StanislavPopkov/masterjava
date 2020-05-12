package ru.javaops.masterjava.controller;

import ru.javaops.masterjava.service.FileUploadService;
import ru.javaops.masterjava.xml.schema.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/upload")
@MultipartConfig()
public class FileUploadServlet extends HttpServlet {
    FileUploadService fileUploadService = new FileUploadService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/upload.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        List<Part> fileParts = req.getParts().stream().filter(part -> "file".equals(part.getName()) && part.getSize() > 0)
                .collect(Collectors.toList());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (Part filePart : fileParts) {
            String fileName = filePart.getSubmittedFileName(); // MSIE fix.
            InputStream fileContent = filePart.getInputStream();
            byte buf[] = new byte[8192];
            int qt = 0;
            while ((qt = fileContent.read(buf)) != -1) {
                baos.write(buf, 0, qt);
            }
        }
        List<User> userList = fileUploadService.getUserList(baos.toByteArray());
        req.setAttribute("userList", userList);
        req.getRequestDispatcher("/users.jsp").forward(req, res);
    }
}