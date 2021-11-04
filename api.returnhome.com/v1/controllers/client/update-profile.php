<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: PUT");

require_once '../../config/connection.php' ;
require_once '../../models/client.php' ;

// CREA LA CONEXION A LA BASE DE DATOS
$pdo = new Connection();

//PHP://INPUT PERMITE LEER DATOS DE UN CUERPO SOLICITADO
$parameters = file_get_contents('php://input');
$data = json_decode($parameters, true);
echo $parameters;
$client = new Client();
$stmt=$client->updateProfile($pdo, $data["id"], $data["name"], $data["email"], $data["gender"], $data["phoneNumber"]);

if($stmt){
    //CLIENTE ACTUALIZADO
    http_response_code(200);
    echo json_encode(array("message"=>"Client has been updated successfully"));
}
else{
    //NO SE PUDO ACTUALIZAR
    http_response_code(304);
    echo json_encode(array("message" => "No client found "));
}
?>