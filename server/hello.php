<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>Document</title>
	<style>
		/*css代码*/
	</style>
	<script>
		// js代码在这里。。。
	</script>
</head>
<body>
	<?php 
		echo '<h1>hello world!</h1>';
		echo date("H:i:s d.m.Y");

		if (mysqli_connect('localhost', 'root', '123456')) {
			echo("数据库mysql连接成功！");
		} else {
			echo("数据库mysql连接失败！");
		}
	?>
</body>
</html>