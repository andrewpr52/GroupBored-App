<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_GET['parentid']) && isset($_GET['followedid'])) {
 
    $parent_user_id = $_GET['parentid'];
    $followed_user_id = $_GET['followedid'];
    
    $sql = "INSERT INTO FollowedUsers (ParentUserID, FollowedUserID) VALUES ('$parent_user_id', '$followed_user_id')";
 
    // connecting to db
    $con = mysqli_connect("localhost", "andrewpr52", "rjja1608", "MessageBoard");
 
	// check if row inserted or not
    if (mysqli_query($con, $sql)) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Successfully followed user.";
 
        // echoing JSON response
        echo json_encode($response, JSON_PRETTY_PRINT);
    }
    else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "An error occurred and the user was not followed.";
 
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
