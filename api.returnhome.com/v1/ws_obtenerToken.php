<?php
$hostname = "localhost";
$username = "root";
$password = "1998*";
$database_name = "dbreturnhome";

if(isset($_GET['idCliente'])){
    $idCliente = $_GET['idCliente'];

    //CONEXION A LA BASE DE DATOS
    $conexion=new PDO("mysql:host=".$hostname.";dbname=".$database_name."",
                                   $username,
                                   $password,
                                   array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));
           
    $stmt = $conexion->prepare("SELECT * FROM tblToken WHERE idCliente = ?");
    $stmt->bindValue(1,$idCliente);

    if($stmt->execute()){

        $array_tokens = array();
    
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){

            extract($row);
            $e = array("token" => $token,
                       "idCliente" => $idCliente);
        
            array_push($array_tokens, $e);
        }
        
        $stmt=null;
        http_response_code(200);
        echo json_encode(array("tokens" => $array_tokens));

    }
    else{
        http_response_code(404);
    }
}
else{
    http_response_code(404);
}

?>