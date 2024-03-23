<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Tela que mostra os erros</title>
</head>
<body>
<h1>Error, entre em contato com suporte.</h1>

<%
out.print(request.getAttribute("msg"));
%>

</body>
</html>