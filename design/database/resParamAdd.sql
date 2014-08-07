-- resID :  ff8080814389c45401438a1a8bbe001a
-- formatID : ff80808147781ffb01478d06361f0293
-- defaultValue: ff80808147781ffb01478d017bff0291



DELIMITER //

CREATE PROCEDURE ADD_RES_DEFAULT_PARAM()
  BEGIN

    DECLARE Done INT DEFAULT 0;
    DECLARE STORE_RES_ID VARCHAR(32);
    DECLARE store_res_csr CURSOR FOR SELECT
                                   ID
                                 FROM STORE_RES
                                 WHERE RES='ff8080814389c45401438a1a8bbe001a';

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET Done = 1;



    OPEN store_res_csr;

    order_loop: LOOP -- Loop through org_grade
      FETCH store_res_csr
      INTO STORE_RES_ID;
      IF Done = 1
      THEN
        LEAVE order_loop;
      END IF;


      INSERT FORMAT(ID,DEFINE,FORMAT_VALUE,STORE_RES) VALUES (STORE_RES_ID,'ff80808147781ffb01478d06361f0293','ff80808147781ffb01478d017bff0291',STORE_RES_ID);

    END LOOP order_loop;
    CLOSE store_res_csr;



  END;//
DELIMITER ;

CALL ADD_RES_DEFAULT_PARAM();

DROP PROCEDURE ADD_RES_DEFAULT_PARAM;