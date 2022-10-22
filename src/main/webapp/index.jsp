<%--<html>--%>
<%--<body>--%>
<%--<h2>Hello World!</h2>--%>
<%--</body>--%>
<%--</html>--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
<%--重定向到登录界面--%>
<%--<%response.sendRedirect(request.getContextPath()+"/user/tologin");%>--%>
<%response.sendRedirect(request.getContextPath()+"/index/toindex");%>

<h2>Hello World!</h2>
</body>
</html>
