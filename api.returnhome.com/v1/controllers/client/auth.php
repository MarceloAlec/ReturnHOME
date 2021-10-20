<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");

require_once '../../config/connection.php' ;
require_once '../../models/client.php' ;

// CREA LA CONEXION A LA BASE DE DATOS
$pdo = new Connection();
$client = new Client();

//PHP://INPUT PERMITE LEER DATOS DE UN CUERPO SOLICITADO
$parameters = file_get_contents('php://input');
$data = json_decode($parameters, true);

//RETORNA LOS DATOS QUE CORRESPONDA AL CLIENTE CON ESE EMAIL Y PASSWORD
$stmt = $client->authClient($pdo,$data["email"],$data["password"]);

if($stmt){
    http_response_code(201);
    $client_auth[] = array("id"=>$stmt["id"],
                    "name"=>$stmt["name"],
                    "email"=>$stmt["email"],
                    "password"=>$stmt["pass"],
                    "gender"=>$stmt["gender"],
                    "phoneNumber"=>$stmt["phoneNumber"]);
    
    echo json_encode(array("message"=>"Client authenticated successfully",
                           "client" => $client_auth));

}
else{
    //AUTENTICACION NO AUTORIZADA
    http_response_code(404);
    echo json_encode(array("message" => "Bad credentials"));
}
                  
?>