<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Manual push</title>
</head>
<body>

<form action="user" method="POST">

nombre<input type="text" name="userId" /><br><br>
ip<input type="text" name="userIp" /><br><br>
servle port<input type="text" name="userServlePort" /><br><br>
dimon port<input type="text" name="userDimonPort" /><br><br>
email<input type="text" name="userEmail" /><br><br>
<input type="submit" value="crear" />

</form>
</body>
</html>