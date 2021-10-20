<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");

require_once '../../config/connection.php' ;
require_once '../../models/client.php' ;

// CREA LA CONEXION A LA BASE DE DATOS
$pdo = new Connection();

//PHP://INPUT PERMITE LEER DATOS DE UN CUERPO SOLICITADO
$parameters = file_get_contents('php://input');
$data = json_decode($parameters, true);

$client = new Client();
$stmt=$client->createClient($pdo, $data["name"], $data["email"], password_hash($data["password"],PASSWORD_DEFAULT), $data["gender"], $data["phoneNumber"] );

if($stmt){
    //CLIENTE CREADO
    http_response_code(201);
    echo json_encode(array("message"=>"Client has been created successfully","client" => $stmt));
}
else{
    //CLIENTE NO AUTORIZADO
    http_response_code(401);
    echo json_encode(array("message" => "Client already exists"));
}
    
?>




