<?php
// Code adapted from freecontactform.com/email_form.php

if (isset($_GET['username']) && isset($_GET['postid'])) {
	$email_to = 'sappi.ws.1@gmail.com';
	$email_subject = 'Post Report - GroupBored';

	$email_message = 'Username: ' . $_GET['username'] . "\n" .
					 'Post ID: ' . $_GET['postid'];
					 
	ini_set("display_errors", 1);
	error_reporting(E_ALL);
	ini_set("SMTP", "aspmx.1.google.com");
	ini_set("sendmail_from", "sappi.ws.1@gmail.com");
	
	$sender = 'sappi.ws.1@gmail.com';
	$recipient = $sender;
	
	$subject = "this is the subject";
	$message = "this is the message";
					 
	$headers = 'From: ' . $sender;
			   
	if (mail($recipient, $subject, $message, $headers)) {
		echo "<p>Message sent successfully. I'll be in contact shortly!</p>";
	}
	else {
		$response["success"] = 0;
		$response["message"] = error_get_last();
		
		// echoing JSON response
		echo json_encode($response, JSON_PRETTY_PRINT);
	}
}

?>
