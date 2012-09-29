<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Manual push</title>
</head>
<body>

<form action="push" method="POST">
Video id<input type="text" name="videoId"><br><br>
Video file name (en el dir cachos)<input type="text" name="fileName"><br><br>
<div id="first">
ip:<input class="ip-0" type="text" name="ip-0">
port:<input class="port-0" type="text" name="port-0">
from:<input class="from-0" type="text" name="from-0">
lenght:<input class="lenght-0" type="text" name="lenght-0">
</div> 
<a href="#" class="unomas">uno mas</a>
<br><br><input type="submit" value="push">
</form>
<script type='text/javascript' src='jquery/jquery-1.8.2.min.js'></script>
<script>
$(function(){
	index = 1;
	$(".unomas").click(function(){
		elem = '<div>ip:<input class="ip-'+index+'" name="ip-'+index+'" type="text">'+
		'port:<input class="port-'+index+'" name="port-'+index+'" type="text">'+
		'from:<input class="from-'+index+'" name="from-'+index+'" type="text">'+
		'lenght:<input class="lenght-'+index+'" name="lenght-'+index+'" type="text">'+
		'</div>' 
		index = index+1;
		alert(index);
		$('#first').after(elem);
	});
});
</script>
</body>
</html>