<?php

	header('Content-type: bitmap; charset=utf-8');
	
	if (isset($_POST['encodedstring'])) {
		$encodedstring = $_POST['encodedstring'];
		$imagename = $_POST['imagename'];
		$uname = $_POST['username'];
		
		$decodedstring = base64_decode($encodedstring);
		
		$dir = 'images/' . $uname;
		$path = $dir . '/' . $imagename;
		
		if (!is_dir($dir)) {
			mkdir($dir, 0777, true);
		}
		
		echo $path;
		$file = fopen($path, 'wb');
		
		$iswritten = fwrite($file, $decodedstring);
		echo $iswritten . "\n";
		fclose($file);
		
		$imageurl = "http://arodsg.com/android_connect/" . $path;
		
		if ($iswritten > 0) {
			$con = mysqli_connect("localhost", "andrewpr52", "rjja1608", "MessageBoard");
			$query = "UPDATE Users SET ProfilePictureURL = '$imageurl' WHERE Username = '$uname'";
			
			$result = mysqli_query($con, $query);
			
			if ($result) {
				echo 'success';
			}
			else {
				echo 'failed1';
			}
			
			mysqli_close($con);
		}
		else {
			echo $iswritten . "\n";
			echo $path . "\n";
			echo getcwd() . "\n";
			echo 'failed2';
		}
		
		// connecting to db
		/*$con = mysqli_connect("localhost", "andrewpr52", "rjja1608", "MessageBoard");
		
		if (mysqli_connect_errno($con)) {
			echo "Failed to connect to MySQL: " . mysqli_connect_error();
		}*/
	}

?>
