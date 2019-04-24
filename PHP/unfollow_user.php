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
    
    $sql = "DELETE FROM FollowedUsers WHERE ParentUserID = '$parent_user_id' AND FollowedUserID = '$followed_user_id'";
 
    // connecting to db
    $con = mysqli_connect("localhost", "andrewpr52", "rjja1608", "MessageBoard");
 
	// check if row deleted or not
    if (mysqli_query($con, $sql)) {
        // successfully deleted from database
        $response["success"] = 1;
        $response["message"] = "Successfully unfollowed user.";
 
        // echoing JSON response
        echo json_encode($response, JSON_PRETTY_PRINT);
    }
    else {
        // failed to delete row
        $response["success"] = 0;
        $response["message"] = "An error occurred and the user was not unfollowed.";
 
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
