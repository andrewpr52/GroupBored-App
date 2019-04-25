<?php
 
/*
 * Following code will check the given login credentials
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if ($_SERVER['REQUEST_METHOD'] == "POST" && !empty($_POST['username']) && !empty($_POST['password'])) {
	
    $uname = $_POST['username'];
    $pword = password_hash($_POST['password'], PASSWORD_DEFAULT);
    //$pword = hash("sha256", $_POST['password']);
 
    // connecting to db
    $con = mysqli_connect("localhost", "andrewpr52", "rjja1608", "MessageBoard");
 
    // mysql get user info if login credentials are correct
    $sql = "SELECT Username FROM Users WHERE Username = '$uname'";
	$result = mysqli_query($con, $sql);
	$row = mysqli_fetch_assoc($result);
	
	// check if user credentials are correct
    if (empty($row)) {
		$defaultbio = "New user";
		$defaultprofileimage = "https://api.adorable.io/avatars/" . $uname;
		$sql2 = "INSERT INTO Users (Username, Password, UserBio, ProfilePictureURL) VALUES ('$uname', '$pword', '$defaultbio', '$defaultprofileimage')";
		
		// check if row inserted or not
		if (mysqli_query($con, $sql2)) {
			$sql_id = "SELECT ID, JoinDate FROM Users WHERE Username = '$uname'";
			$result_idjoindate = mysqli_query($con, $sql_id);
			$row_idjoindate = mysqli_fetch_assoc($result_idjoindate);
			
			$user = array();
			$user["ID"] = str_replace("&quot;", "'", htmlspecialchars($row_idjoindate['ID']));
			$user["Username"] = str_replace("&quot;", "'", htmlspecialchars($uname));
			$user["Password"] = str_replace("&quot;", "'", htmlspecialchars($pword));
			$user["UserBio"] = str_replace("&quot;", "'", htmlspecialchars($defaultbio));
			$user["JoinDate"] = str_replace("&quot;", "'", htmlspecialchars($row_idjoindate['JoinDate']));		
			
			// successfully signed up user
			$response["success"] = 1;
			$response["message"] = "User successfully signed up.";

			$response["user"] = array();
			array_push($response["user"], $user);
	 
			// echoing JSON response
			echo json_encode($response, JSON_PRETTY_PRINT);
		}
		else {
			// failed to insert row
			$response["success"] = 0;
			$response["message"] = "An error occurred and the user was not signed up.";
	 
			// echoing JSON response
			echo json_encode($response, JSON_PRETTY_PRINT);
		}
    }
    else {
        // failed to signup
        $response["success"] = 0;
        $response["message"] = "Signup failed, username already in use.";
 
        // echoing JSON response
        echo json_encode($response, JSON_PRETTY_PRINT);
    }
}
else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) missing.";
 
    // echoing JSON response
    echo json_encode($response, JSON_PRETTY_PRINT);
}
?>
