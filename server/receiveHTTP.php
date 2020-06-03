<?php

	$name = "";
	$DOB = "";
	$info = "";
	$ip = "";

	if ( isset($_POST["name"]) ) {
		$name = $_POST["name"];
		$DOB = $_POST["DOB"];
		$info = $_POST["info"];
		$ip = $_SERVER["REMOTE_ADDR"];

		echo "<br>name is: $name";
		echo "<br>DOB is: $DOB";
		echo "<br>info is: $info";
		echo "<br>patient's ip is: $ip";
	}

?>