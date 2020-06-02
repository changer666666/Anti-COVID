<?php
	require __DIR__.'/vendor/autoload.php';
	use Kreait\Firebase\Factory;

	$factory = (new Factory())->withDatabaseUri('https://anti-covid-5e9df.firebaseio.com');

	// $factory = (new Factory())->withDatabaseUri('https://anticovid-a242d.firebaseio.com');

	$database = $factory->createDatabase();

	$reference = $database->getReference('/users/2');
	// $reference = $database->getReference('/patients');
	// $reference = $database->getReference('/Waiting List');

	//set or replace value in database
	// $reference
 //    ->set([
 //       // 'name' => 'a2',
 //       // 'emails' => 'salesa2@domain.tld',
 //       // 'website' => 'https://app.domain.tlda2',
 //      ]);

	//query values in database
	$snapshot = $reference->orderByChild('name')->limitToFirst(1)->getSnapshot()->remove();
	$snapshotAll = $reference->orderByChild('name')->getSnapshot();
	$value = $snapshot->getValue();
	$valueAll = $snapshotAll->getValue();

	// $queue = new SplQueue();
	// $queue->enqueue('A Patient');
	// $queue->enqueue('B Patient');
	// $queue->enqueue('C Patient');
	echo ("<pre>");
	print_r($value);
	echo ("</pre>");


	$firebase->getAuth()->deleteUser('4');

	// $key = array_search($value, (array)$valueAll);
	// print_r($key);

	// $child = $snapshot->hasChild();
	// print_r($child);


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