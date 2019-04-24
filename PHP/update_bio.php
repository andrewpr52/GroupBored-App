<?php
 
/*
 * Following code will check the given login credentials
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if ($_SERVER['REQUEST_METHOD'] == "POST") {
	
    $uid = $_POST['userid'];
    $bio = $_POST['bio'];
 
    // connecting to db
    $con = mysqli_connect("localhost", "andrewpr52", "rjja1608", "MessageBoard");
    
    // escape strings
    $bio = mysqli_real_escape_string($con, $bio);
    $uid = mysqli_real_escape_string($con, $uid);
 
    // mysql update query
    $sql = sprintf("UPDATE Users SET UserBio = '%s' WHERE ID = '%s'", mysqli_real_escape_string($con, $bio), mysqli_real_escape_string($con, $uid));
 
	// check if bio was updated or not
    if (mysqli_query($con, $sql)) {
        // successfully updated UserBio in database
        $response["success"] = 1;
        $response["message"] = "Bio successfully updated.";
 
        // echoing JSON response
        echo json_encode($response, JSON_PRETTY_PRINT);
    }
    else {
        // failed to update bio
        $response["success"] = 0;
        $response["message"] = "An error occurred and the bio was not updated.";
 
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
