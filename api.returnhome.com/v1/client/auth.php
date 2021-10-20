<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");

require_once '../connection.php' ;

// CREA LA CONEXION A LA BASE DE DATOS
$pdo = new Connection();

 //PHP://INPUT PERMITE LEER DATOS DE UN CUERPO SOLICITADO
 $parameters = file_get_contents('php://input');
 $data = json_decode($parameters, true);

$sqlQuery = "select * from tblclient where email=?";
//MEDIANTE PREPARE() SE CREA UNA INSTANCIA DE LA CLASE PDO STATEMENT
//PARA PASAR LOS VALORES O EJECUTAR SENTENCIAS
$stmt = $pdo->prepare($sqlQuery);
$email = $data["email"];
$password = $data["password"];
//CON BINDVALUE SE ENLAZA EL VALOR DE LA VARIABLE
$stmt->bindValue(1,$email);
$stmt->execute();
$num = $stmt->rowCount();

if($num>0){

    $row = $stmt->fetch(PDO::FETCH_ASSOC);

    // VERIFICA LA CONSTRASEÑA
    if (password_verify($password, $row["pass"])) {
        http_response_code(200);
        echo json_encode(
            array("message" => "OK")
        );
    } else {
        http_response_code(404);
        echo json_encode(
            array("message" => "Bad password")
        );
    }
}
else{
    http_response_code(404);
        echo json_encode(
            array("message" => "Bad credentials")
        );
}

?>