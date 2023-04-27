<?php

function sendFCM() {
    $keyU=$_POST['key'];
    $menU=$_POST['men'];
  // FCM API Url
  $url = 'https://fcm.googleapis.com/fcm/send';

  // Put your Server Key here
  $apiKey = "AAAALZrvESA:APA91bGa-EOtWFE2d9y_vUl6ULvzEF7y75iFl6l6F_GPr13UfBPNTvziy9lCJ8gbCxoMpnML801pI4QA_YUmo0LRSHehIAHos-c5XXX3dmY1107kWmttqjx5Mz7gcdvDpReSe_5vIBcY";

  // Compile headers in one variable
  $headers = array (
    'Authorization:key=' . $apiKey,
    'Content-Type:application/json'
  );

  // Add notification content to a variable for easy reference
  $notifData = [
    'title' => "Test Title",
    'body' =>$menU,
    //  "image": "url-to-image",//Optional
    //'click_action' => "activities.NotifHandlerActivity" //Action/Activity - Optional
  ];

  $dataPayload = ['to'=> 'My Name',
  'points'=>80,
  'other_data' => 'This is extra payload'
  ];

  // Create the api body
  $apiBody = [
    'notification' => $notifData,
    'data' => $dataPayload, //Optional
    'time_to_live' => 600, // optional - In Seconds
    //'to' => '/topics/mytargettopic'
    //'registration_ids' = ID ARRAY
    'to' => $keyU
    //'degK0wfWQjm_r46LIT4fki:APA91bE6F3ogRWsiMp33dXbtt9iiTpr2BLGDnh4e9oeln8bjz3WBmNphpjrBIc8KCEePU95pLYcyku7ty4ES8ZpLOPSgrSFzBxD53UoU6SmVTv1Ibuha0C7sz8LGj_3rU1ioCkq4OtIS'
  ];

  // Initialize curl with the prepared headers and body
  $ch = curl_init();
  curl_setopt ($ch, CURLOPT_URL, $url);
  curl_setopt ($ch, CURLOPT_POST, true);
  curl_setopt ($ch, CURLOPT_HTTPHEADER, $headers);
  curl_setopt ($ch, CURLOPT_RETURNTRANSFER, true);
  curl_setopt ($ch, CURLOPT_POSTFIELDS, json_encode($apiBody));

  // Execute call and save result
  $result = curl_exec($ch);
  print($result);
  // Close curl after call
  curl_close($ch);

  return $result;
}
sendFCM();
?>