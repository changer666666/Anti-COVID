<?php

	$queue = new SplQueue();
	$queue->enqueue('A Patient');
	$queue->enqueue('B Patient');
	$queue->enqueue('C Patient');


	$name = "";
	$DOB = "";
	$info = "";
	$ip = "";
	$doctorName = "";
	$doctorInfo = "";


	if ( isset($_GET["name"]) ) {
		$name = $_GET["name"];
		$DOB = $_GET["DOB"];
		$info = $_GET["info"];
		$ip = $_SERVER["REMOTE_ADDR"];



		echo "<br>name is: $name";
		echo "<br>DOB is: $DOB";
		echo "<br>info is: $info";
		echo "<br>patient's ip is: $ip";

		//enqueue patient
		//invoke database, add one patient(id increment), save android ip



	}


	if ( isset($_GET["doctorName"]) ) {
		$doctorName = $_GET["doctorName"];
		$doctorInfo = $_GET["doctorInfo"];

		echo "<br>doctorName is: $doctorName";
		echo "<br>doctorInfo is: $doctorInfo";

		//dequeue current patient
		//invoke database, delete the min id patient.

		//send notification to android.(need android ip)

	}



	
?>