# cqrs-design-pattern

### CreateProduct Event

```
curl --location --request POST 'http://localhost:8080/api/v1/products' \
--header 'Content-Type: application/json' \
--data-raw '{
  "id": "",
  "sku": "123",
  "name": "Earphone",
  "description": "Earphone desc",
  "price": 200
}'
```
### UpdateProduct

```
curl --location --request PUT 'http://localhost:8080/api/v1/products/updated' \
--header 'sku: "123"' \
--header 'Content-Type: application/json' \
--data-raw '{
  "name": "Earphone updated",
  "description": "Earphone desc updated",
  "price": 300
}'
```
