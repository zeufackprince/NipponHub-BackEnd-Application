# Nipponhub API Documentation

## Overview
This documentation describes all REST endpoints available in the Nipponhub service, request inputs, response outputs, and common errors with solutions.

Base URL: `http://localhost:8080`

---

## 1) Product Endpoints
Base path: `/api/v0/product`

### 1.1 POST /api/v0/product/newProduct
Creates a new product with optional image upload(s).

Consumes: `multipart/form-data`
Returns:
- `201 Created` with `ProductDto` on success
- `400 Bad Request` with `{ "error": "..." }` if validation fails

Request fields:
- `ProdName` (string, optional but should be provided)
- `UnitPrice` (decimal)
- `SoldPrice` (decimal)
- `ProdQty` (integer)
- `ProdUrl` (file[]; one or more images)
- `Country` (string[], country names)
- `category` (string)
- `prodDescription` (string)

Example curl:
```bash
curl -v -X POST "http://localhost:8080/api/v0/product/newProduct" \
  -F "ProdName=Blue Backpack" \
  -F "UnitPrice=10.00" \
  -F "SoldPrice=15.00" \
  -F "ProdQty=20" \
  -F "category=Backpacks" \
  -F "prodDescription=Waterproof travel backpack" \
  -F "Country=Japan" \
  -F "ProdUrl=@./backpack.jpg" 
```

Success response sample:
```json
{
  "IdProd": 1,
  "ProdName": "Blue Backpack",
  "UnitPrice": 10.00,
  "SoldPrice": 15.00,
  "ProdQty": 20,
  "Message": "Success",
  "categoryName": "Backpacks",
  "countries": ["Japan"],
  "ProdUrl": ["<fileId1>"]
}
```

Common errors:
- `400 Bad Request` with message `"Invalid input"` or similar from service validation. Fix: ensure required fields are provided and numeric values are valid.
- `500 Internal Server Error`: typically from file upload/IO failure. Fix: verify disk and Mongo GridFS availability.

---

### 1.2 PUT /api/v0/product/updateProduct/{idProd}
Updates an existing product.

Consumes: `multipart/form-data`
Returns:
- `200 OK` with updated `ProductDto` on success
- `500 Internal Server Error` with `ProductDto` containing `message` if update failed

Path parameter:
- `idProd` (Long) product ID

Optional update fields (same as newProduct):
- `ProdName`, `UnitPrice`, `SoldPrice`, `ProdQty`, `ProdUrl`, `Country`, `category`, `prodDescription`

Example:
```bash
curl -v -X PUT "http://localhost:8080/api/v0/product/updateProduct/1" \
  -F "ProdName=Updated Bag" \
  -F "ProdQty=15"
```

Common errors and resolutions:
- If the service returns an object with `message` field and status `500`: check that `idProd` exists and the update operation is permitted; inspect logs for stack traces.

---

### 1.3 GET /api/v0/product/all
Returns all products.

Returns: `200 OK` array of `ProductDto`.

Example:
```bash
curl http://localhost:8080/api/v0/product/all
```

---

### 1.4 GET /api/v0/product/{idProd}
Retrieve product by ID.

Path parameter: `idProd` (Long)
Returns:
- `200 OK` with `ProductDto` if found
- `404 Not Found` with `ProductDto` containing `message` on missing product

Example:
```bash
curl http://localhost:8080/api/v0/product/1
```

---

### 1.5 GET /api/v0/product/searchByName?prodName=...
Search a single product by name (case-insensitive).

Query parameter: `prodName` (string)
Returns:
- `200 OK` with `ProductDto` if found
- `404 Not Found` with `ProductDto` containing `message` if not found

Example:
```bash
curl "http://localhost:8080/api/v0/product/searchByName?prodName=Blue%20Backpack"
```

---

### 1.6 GET /api/v0/product/searchByCountry?country=...
Search products by country.

Query parameter: `country` (string)
Returns: `200 OK` list of `ProductDto`.

Example:
```bash
curl "http://localhost:8080/api/v0/product/searchByCountry?country=Japan"
```

---

### 1.7 GET /api/v0/product/greetings
Health-check endpoint.
Returns: `200 OK` plain string.

Example:
```bash
curl http://localhost:8080/api/v0/product/greetings
```

---

## 2) Category Endpoints
Base path: `/api/v0/categories`

### 2.1 POST /api/v0/categories/createCategory
Creates a new category.

Consumes: JSON
Consumes schema from `CategoryDto`:
```json
{
  "catProdName": "Bags",
  "catProdDes": "Travel and laptop bags"
}
```

Returns:
- `201 Created` with `{ "message": "Category created successfully" }` (or service success text)
- `500 Internal Server Error` with `{ "message": "Error ..." }` if service fails

Example:
```bash
curl -X POST http://localhost:8080/api/v0/categories/createCategory \
  -H "Content-Type: application/json" \
  -d '{"catProdName":"Bags","catProdDes":"Backpacks"}'
```

Common errors:
- `500 Error` if the category name duplicates existing values or DB insertion fails; fix by using valid unique values and checking DB constraints.

---

### 2.2 GET /api/v0/categories/getAllCategories
Returns: `200 OK` list of `CategoryDto`.

Example:
```bash
curl http://localhost:8080/api/v0/categories/getAllCategories
```

---

### 2.3 GET /api/v0/categories/{id}
View category by ID.

Path param: `id` (Long)
Returns:
- `200 OK` category object
- `404 Not Found` with `{ "error": "Category not found with id: <id>" }`

Example:
```bash
curl http://localhost:8080/api/v0/categories/1
```

Error resolution:
- If 404, verify the ID exists with `getAllCategories` or correct the ID.

---

## 3) Country Endpoints
Base path: `/api/v0/country`

### 3.1 POST /api/v0/country/newCountry
Creates a country record.

Consumes JSON with `CountryDto` fields:
```json
{
  "countryName": "Japan",
  "countryCode": "JP"
}
```
Returns: `200 OK` with created `CountryDto`.

Example:
```bash
curl -X POST http://localhost:8080/api/v0/country/newCountry \
  -H "Content-Type: application/json" \
  -d '{"countryName":"Japan","countryCode":"JP"}'
```

Common errors:
- `400` if request body is malformed. Fix: ensure valid JSON.

---

### 3.2 DELETE /api/v0/country/deleteCountry/{idCountry}
Deletes a country.

Note: The endpoint signature uses `@RequestParam Long idCountry` but path declares `{idCountry}` incorrectly. In practice, call:
`DELETE /api/v0/country/deleteCountry?idCountry=<id>` (or adjust code to use path variable).

Returns: `200 OK` string message.

Example:
```bash
curl -X DELETE "http://localhost:8080/api/v0/country/deleteCountry?idCountry=1"
```

Error resolution:
- If 400 due to missing idCountry, pass query parameter or update controller to `@PathVariable`.

---

### 3.3 GET /api/v0/country/getAllCountries
Returns all countries.

Returns: `200 OK` array of `Country` models.

Example:
```bash
curl http://localhost:8080/api/v0/country/getAllCountries
```

---

## 4) File Upload + GridFS Endpoints
Base path: `/file`

### 4.1 POST /file/upload
Upload a single file.

Consumes: `multipart/form-data` with `file` part.
Optional param: `productId` (Long)
Returns:
- `201 Created` JSON with `fileId` and message
- `400 Bad Request` if file missing or invalid
- `500 Internal Server Error` on storage failure

Example:
```bash
curl -X POST http://localhost:8080/file/upload \
  -F "file=@./test.jpg" \
  -F "productId=1"
```

### 4.2 POST /file/upload/batch
Upload multiple files.

Consumes `multipart/form-data` with `files` list and optional `productId`.
Returns:
- `201 Created` with `fileIds`, `count`, `message`
- `400` if invalid
- `500` for IO failure

Example:
```bash
curl -X POST http://localhost:8080/file/upload/batch \
  -F "files=@./img1.png" -F "files=@./img2.png" \
  -F "productId=1"
```

### 4.3 GET /file/{fileId}
Streams image bytes by GridFS file id.
Returns:
- `200 OK` binary image
- `400 Bad Request` invalid id format
- `404 Not Found` if file missing

Example:
```bash
curl http://localhost:8080/file/6500f7b8a4440f1aaad12f65 --output downloaded.jpg
```

### 4.4 GET /file/metadata/{fileId}
Return metadata for file id.

Returns:
- `200 OK` FileDocument metadata
- `404 Not Found` if metadata missing

### 4.5 GET /file/product/{productId}
Return file metadata list for a product.

Returns:
- `200 OK` list of `FileDocument`
- `404 Not Found` if none

Example:
```bash
curl http://localhost:8080/file/product/1
```

### 4.6 DELETE /file/{fileId}
Delete file by GridFS id.

Returns:
- `200 OK` message
- `400` invalid id
- `500` on delete failure

Example:
```bash
curl -X DELETE http://localhost:8080/file/6500f7... 
```

---

## 5) Achat (Purchase) Endpoints
Base path: `/api/v0/achat`

### 5.1 POST /api/v0/achat/new-achat
Creates a purchase and updates stock.

Consumes JSON with `Achats` model.
Use the `Achats` model from `Models/Achats.java` (fields used by service). Typically includes `date`, `AchatItem` list and totals.

Returns: `201 Created` `AchatDto`.

Example:
```bash
curl -X POST http://localhost:8080/api/v0/achat/new-achat \
  -H "Content-Type: application/json" \
  -d '{"date":"2026-03-17","items":[{"prodId":1,"qte":5,"unitPrice":10}],"coutTotal":50,"totalItem":1}'
```

### 5.2 GET /api/v0/achat/get-all-achat
Returns all purchase records as `AchatDto` list.

Example:
```bash
curl http://localhost:8080/api/v0/achat/get-all-achat
```

---

## 6) Vente (Sale) Endpoints
Base path: `/api/v0/vente`

### 6.1 POST /api/v0/vente/register
Registers a sale, deducts stock and calculates gain.

Consumes JSON `Vente` model.
Returns: `201 Created` `VenteDto`.

Example:
```bash
curl -X POST http://localhost:8080/api/v0/vente/register \
  -H "Content-Type: application/json" \
  -d '{"date":"2026-03-17","items":[{"prodId":1,"qte":2,"soldPrice":20}],"prixVendu":40,"coutTotal":20}'
```

### 6.2 GET /api/v0/vente/all
Returns all sale records. `200 OK` list of `VenteDto`.

Example:
```bash
curl http://localhost:8080/api/v0/vente/all
```

### 6.3 GET /api/v0/vente/{id}
Get sale by id.

Example:
```bash
curl http://localhost:8080/api/v0/vente/1
```

### 6.4 GET /api/v0/vente/search?date=YYYY-MM-DD
Search all sales by date.

Example:
```bash
curl "http://localhost:8080/api/v0/vente/search?date=2026-03-17"
```

---

## 7) Common Error Meanings and Fixes

### 400 Bad Request
- Invalid JSON in `@RequestBody`.
- Missing required query/path parameter.
- Invalid ID format (e.g. GridFS id not valid hex String).

Fix: Validate request format, use correct parameter names, ensure numeric/JSON values are correct.

### 404 Not Found
- Requested resource not present (product, category, file metadata, sale, purchase).

Fix: Confirm IDs via list endpoints (`all`) and use the correct ID.

### 500 Internal Server Error
- Database errors (MySQL or Mongo). Could be connection refused, constraint violation, unknown field, or file IO.
- For file uploads, this often means GridFS storage or disk problem.

Fix: Check server logs, confirm database accessibility, verify request values, and ensure dependencies (MySQL + Mongo) are running.

### `message` in DTO
Some endpoints return DTOs with `message` field when service-level operations fail.
- If API returns status `500` with `ProductDto.message`, check service logic and input dataset.
- If message indicates not found or update failed, either create the entity first or supply valid IDs.

---

## 8) Quick Troubleshooting
1. Use `GET /api/v0/product/greetings` to verify service is running.
2. Confirm MySQL and Mongo are healthy (connections can fail silently in service logic).
3. For file uploads, verify file key is `file` for single and `files` for batch.
4. If one endpoint returns `null` response fields, verify DTO request fields map exactly.

---

## 9) Notes
- For `CountryController.deleteCountry`, the method uses `@RequestParam`. Call as query param `?idCountry=`.
- For category creation, ensure the JSON field names are exactly `catProdName` and `catProdDes`.
- For update product, any optional param can be omitted; existing values are preserved by service logic.


