## Клиент и сервер, использующие mTLS - взаимную аутентификацию.

1. Запустиь сервер: ServerWithCode.java (вариант с загрузкой cacerts и keystore программно) или ServerWithVMOptions.java (загрузка cacerts и keystore через JVM options). Не забыть поменять пути до файлов

2. Запустить клиент: ClientWithCode.java или ClientWithVMOptions.java. Не забыть поменять пути до файлов на нормальные


#### Как создать cacerts и keystore с нуля без регистрации и смс :credit_card::

1. Создаем keystore сервера с его собственным сертом:
   `keytool -genkey -keyalg DSA -keypass changeit -storepass changeit -keystore serverkeystore.jks`

2. Экспортируем из него серт СА *serverCA.cer* для дальнейшего импорта в truststore клиента:
   `keytool -export -storepass changeit -file serverCA.cer -keystore serverkeystore.jks`

3. Создаем keystore клиента:
   `keytool -genkey -keyalg DSA -keypass changeit -storepass changeit -keystore clientkeystore.jks`

4. Экспортируем из него серт СА *clientCA.cer* для дальнейшего импорта в truststore сервера:
   `keytool -export -storepass changeit -file clientCA.cer -keystore clientkeystore.jks`
   
5. Создаем truststore сервера:
   `keytool -import -v -trustcacerts -file clientCA.cer -keypass changeit -storepass changeit -keystore servertruststore.jks`
   
6. Создаем truststore клиента:
   `keytool -import -v -trustcacerts -file serverCA.cer -keypass changeit -storepass changeit -keystore clienttruststore.jks`
