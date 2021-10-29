<?php

class Client{

    private $tblClient = "tblclient";

    public function __construct(){

    }

    public function authClient($connection, $email, $password){
        $query = "SELECT * FROM " . $this->tblClient . " WHERE email = ?";
        //MEDIANTE PREPARE() SE CREA UNA INSTANCIA DE LA CLASE PDO STATEMENT
        //PARA PASAR LOS VALORES O EJECUTAR SENTENCIAS
        $stmt = $connection->prepare($query);
        //CON BINDVALUE SE ENLAZA EL VALOR DE LA VARIABLE
        $stmt->bindValue(1,$email);
        $stmt->execute();

        $num = $stmt->rowCount();

        if($num>0){
            $result = $stmt->fetch(PDO::FETCH_ASSOC);
            // VERIFICA LA CONSTRASEÑA
            if (password_verify($password, $result["pass"])) {
                return $result;
            } else {
                return false;
            }
        }
        else{
            return false;
        }
    }

    public function createClient($connection, $name, $email, $password, $gender, $phoneNumber){

        $query = "SELECT * FROM " . $this->tblClient . " WHERE email = ?";

        $stmt = $connection->prepare($query);
        $stmt->bindValue(1,$email);
        $stmt->execute();

        $num = $stmt->rowCount();

        if($num == 0){
            $query = "INSERT INTO ".$this->tblClient . "(name, email, pass, gender, phoneNumber) values(?, ?, ?, ?, ?)";

            $stmt = $connection->prepare($query);
            $stmt->bindValue(1,$name);
            $stmt->bindValue(2,$email);
            $stmt->bindValue(3,$password);
            $stmt->bindValue(4,$gender);
            $stmt->bindValue(5,$phoneNumber);
            $stmt->execute();

            $idClient=$connection->lastInsertId();
            return array("id"=>$idClient);
        }
        else{
            return false;
        }
    }


    





}



?>