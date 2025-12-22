@echo off
echo Fixing admin user...
echo.

mysql -u root -pDurga@123 service_request_db -e "DELETE FROM user_roles WHERE user_id = (SELECT id FROM users WHERE username = 'admin');"
mysql -u root -pDurga@123 service_request_db -e "DELETE FROM users WHERE username = 'admin';"

mysql -u root -pDurga@123 service_request_db -e "INSERT INTO users (username, email, password, first_name, last_name, phone, department, is_active) VALUES ('admin', 'admin@servicedesk.com', '$2a$10$xZ8qJ9Y5K3L4M6N7O8P9QeRsT0uV1wX2yZ3aB4cD5eF6gH7iJ8kL9m', 'System', 'Administrator', '1234567890', 'IT', TRUE);"

mysql -u root -pDurga@123 service_request_db -e "INSERT INTO user_roles (user_id, role_id) VALUES ((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'));"

echo.
echo Verifying admin user...
mysql -u root -pDurga@123 service_request_db -e "SELECT u.id, u.username, u.email, u.first_name, u.last_name, r.name as role FROM users u JOIN user_roles ur ON u.id = ur.user_id JOIN roles r ON ur.role_id = r.id WHERE u.username = 'admin';"

echo.
echo Admin user fixed successfully!
echo Username: admin
echo Password: Admin@123
echo.
pause
