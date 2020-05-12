<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: I am
  Date: 11.05.2020
  Time: 22:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<table border="1" cellpadding="8" cellspacing="0">
    <thead>
    <tr>
        <th>Name</th>
        <th>email</th>
        <th>flag</th>
    </tr>
    </thead>
    <c:forEach items="${userList}" var="user">
        <jsp:useBean id="user" type="ru.javaops.masterjava.xml.schema.User"/>
        <tr>
            <td>${user.value}</td>
            <td>${user.email}</td>
            <td>${user.flag}</td>
        </tr>
    </c:forEach>
</table>

</body>
</html>
