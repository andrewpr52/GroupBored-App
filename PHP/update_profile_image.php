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
		
		//echo $path;
		$file = fopen($path, 'wb');
		
		$iswritten = fwrite($file, $decodedstring);
		//echo $iswritten . "\n";
		fclose($file);
		
		$imageurl = "http://arodsg.com/android_connect/" . $path;
		
		if ($iswritten > 0) {
			$con = mysqli_connect("localhost", "andrewpr52", "rjja1608", "MessageBoard");
			$query = "UPDATE Users SET ProfilePictureURL = '$imageurl' WHERE Username = '$uname'";
			
			$result = mysqli_query($con, $query);
			
			if ($result) {
				// successfully updated ProfilePicture in database
				$response["success"] = 1;
				$response["message"] = "Profile picture successfully updated.";
			}
			else {
				// successfully updated ProfilePicture in database
				$response["success"] = 0;
				$response["message"] = "An error occurred and the profile picture was not updated.";
			}
			// echoing JSON response
			echo json_encode($response, JSON_PRETTY_PRINT);
			
			mysqli_close($con);
		}
		else {
			$response["success"] = 0;
			$response["message"] = "An error occurred and the profile picture was not uploaded.";
			// echoing JSON response
			echo json_encode($response, JSON_PRETTY_PRINT);
		}
	}
?>
