<?php
	require __DIR__.'/vendor/autoload.php';
	use Kreait\Firebase\Factory;

	$factory = (new Factory())->withDatabaseUri('https://anticovid-a242d.firebaseio.com');

	$database = $factory->createDatabase();

	//set or replace value in database
	// $reference
 //    ->set([
 //       'name' => 'a2',
 //       'emails' => 'salesa2@domain.tld',
 //       'Timestamp' => '0120560189402',
 //      ]);


	//delete
	// $ref = $reference->orderByChild('Timestamp')->limitToFirst(1)->getSnapshot()->getReference();
	// $ref->remove();

	// $key = array_search($value, (array)$valueAll);
	// print_r($key);

	// $child = $snapshot->hasChild();
	// print_r($child);

	//send http request to android
	// $url = 'http://localhost/receiveHTTP.php';
	// $data = http_build_query( array( 'name' => 'value' ) );
	// $options = array(
	//     'http' => array(
	//         'header'  => "Content-type: application/x-www-form-urlencoded",
	//         'method'  => 'POST',
	//         'content' => $data,
	//     ),
	// );
	// $context = stream_context_create( $options );
	// $result = file_get_contents( $url, false, $context );
	// echo "send request";

	$name = "";
	$email = "";
	$password = "";
	$doctorName = "";
	$doctorInfo = "";

	if ( isset($_GET["name"]) ) {
		$name = $_GET["name"];
		$email = $_GET["patientemail"];
		$password = $_GET["password"];

		echo "<br>name is: $name";
		echo "<br>email is: $email";
		echo "<br>password is: $password";

		//1. save data into patients table.
		$curSnapshot = $database->getReference('/patients/'.$name);
		$curSnapshot->set([
			'Timestamp' => '00000',
			'email' => $email
		]);

		//2. save data into waiting list table.
		$curSnapshot = $database->getReference('/Waiting List/'.$name);
		$curSnapshot->set([
			'Timestamp' => '00000',
			'email' => $email
		]);
		
	}


	if ( isset($_GET["doctorName"]) ) {
		$doctorName = $_GET["doctorName"];
		$doctorInfo = $_GET["doctorInfo"];

		echo "<br>doctorName is: $doctorName";
		echo "<br>doctorInfo is: $doctorInfo";

		//dequeue current patient
		//invoke database, delete the earliest patient.

		//1. query values in waiting list, ordered by timestamp.
		//get the earlist patient in waiting list.
		$earlySnapshot = $database->getReference('/Waiting List')->orderByChild('Timestamp')->limitToFirst(1)->getSnapshot();
		$earlyValue = $earlySnapshot->getValue();

		echo ("<pre>");
		echo "earlyPatient";
		print_r($earlyValue);
		echo ("</pre>");

		//2. set curPatient to be next petient
		$curPatientRef = $database->getReference('/curPatient');
		echo ("<pre>");
		print_r($curPatientRef->getSnapshot()->getValue());
		echo ("</pre>");
		foreach ($earlyValue as $key => $patient){
	    	$curEmail = "{$patient['email']}";
		}
		$curPatientRef->set($curEmail);

		//3. set isAvailable to be true. Android will detect this field change.
		$database->getReference('/isAvailable')->set("True");

		//4.  delete ealiest patient in waiting list.
		foreach ($earlyValue as $key => $person){
	    	$number = $key;
	    	// echo "$number";
			$database->getReference('/Waiting List/'.$number)->set([]);
		}
	}



	
?>