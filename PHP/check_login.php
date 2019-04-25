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
    $pword = $_POST['password'];
 
    // connecting to db
    $con = mysqli_connect("localhost", "andrewpr52", "rjja1608", "MessageBoard");
 
    // mysql get user info if login credentials are correct
    $sql = "SELECT * FROM Users WHERE Username = '$uname'";
	$result = mysqli_query($con, $sql);
	$row = mysqli_fetch_assoc($result);
	
	// check if user credentials are correct
    if (!empty($row)) {
		$user = array();
		$user["ID"] = str_replace("&quot;", "'", htmlspecialchars($row["ID"]));
		$user["Username"] = str_replace("&quot;", "'", htmlspecialchars($row["Username"]));
		$user["Password"] = str_replace("&quot;", "'", htmlspecialchars($row["Password"]));
		$user["UserBio"] = str_replace("&quot;", "'", htmlspecialchars($row["UserBio"]));
		$user["JoinDate"] = str_replace("&quot;", "'", htmlspecialchars($row["JoinDate"]));
		
		if (password_verify($pword, $user["Password"])) {
			// successful login
			$response["success"] = 1;
			$response["message"] = "User successfully logged in.";

			$response["user"] = array();
			array_push($response["user"], $user);
	 
			// echoing JSON response
			echo json_encode($response, JSON_PRETTY_PRINT);
		}
		else {
			// unsuccessful login
			$response["success"] = 0;
			$response["message"] = "Incorrect password, please try again.";
	 
			// echoing JSON response
			echo json_encode($response, JSON_PRETTY_PRINT);
		}
    }
    else {
        // failed to login in
        $response["success"] = 0;
        $response["message"] = "Login failed, check info and try again.";
 
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
