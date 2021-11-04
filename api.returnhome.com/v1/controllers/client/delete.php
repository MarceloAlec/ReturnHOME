<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

require_once '../../config/connection.php' ;
require_once '../../models/client.php' ;

// CREA LA CONEXION A LA BASE DE DATOS
$pdo = new Connection();
// OBTENER VALOR DEL PARAMETRO ID
$id = isset($_GET['id']) ? $_GET['id'] : die();

$client = new Client();
$stmt = $client->deleteClient($pdo,$id);

if($stmt){
    //CUENTA ELIMINADA
    http_response_code(200);
    echo json_encode(array("message" => "Your account was successfully deleted"));
}
else{
    //NO SE PUDO COMPLETAR EL PROCESO
    http_response_code(404);
    echo json_encode(array("message" => "No client found"));

}

?>