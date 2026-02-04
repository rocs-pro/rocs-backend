# GRN (Goods Received Note) System - Complete Implementation

## Overview
This is a complete GRN system for inventory management with full CRUD operations, approval workflow, stock management integration, and comprehensive API endpoints.

## Database Schema Used
- **grns** - Main GRN table with branch, supplier, PO references
- **grn_items** - GRN line items with product details, quantities, and pricing
- **stock** - Stock levels updated automatically when GRN is approved
- **batches** - Product batches created from GRN items with expiry dates
- **suppliers** - Supplier master data
- **products** - Product master data
- **branches** - Branch master data
- **user_profiles** - User management for creators/approvers

## Key Features

### 1. GRN Creation
- Creates GRN with auto-generated GRN numbers (format: GRN-{branchId}-{date}-{sequence})
- Validates suppliers and products
- Calculates totals automatically
- Supports both PO-based and direct GRNs
- Creates GRN items with batch tracking

### 2. Approval Workflow
- Three status levels: PENDING, APPROVED, REJECTED
- Only PENDING GRNs can be approved/rejected
- Stock levels updated only on approval
- Audit trail with approver information

### 3. Stock Management Integration
- Automatic stock updates on GRN approval
- Creates/updates stock records per branch-product
- Handles available quantity calculations
- Batch creation for products with expiry dates

### 4. Comprehensive API Endpoints
```
POST   /api/inventory/grn                     - Create new GRN
GET    /api/inventory/grn/{grnId}             - Get GRN by ID
GET    /api/inventory/grn/branch/{branchId}   - Get GRNs by branch
POST   /api/inventory/grn/search              - Search GRNs with filters
PUT    /api/inventory/grn/{grnId}             - Update GRN (PENDING only)
PUT    /api/inventory/grn/{grnId}/approve     - Approve GRN
PUT    /api/inventory/grn/{grnId}/reject      - Reject GRN
PUT    /api/inventory/grn/{grnId}/payment-status - Update payment status
DELETE /api/inventory/grn/{grnId}             - Delete GRN (PENDING only)
GET    /api/inventory/grn/branch/{branchId}/stats - Get GRN statistics
GET    /api/inventory/grn/product/{productId}/items - Get GRN items by product
GET    /api/inventory/grn/check-number/{grnNo} - Check if GRN number exists
GET    /api/inventory/grn/supplier/{supplierId} - Get GRNs by supplier
GET    /api/inventory/grn/pending              - Get pending GRNs
```

## File Structure

### Entities
- `com.nsbm.rocs.entity.inventory.GRN` - Main GRN entity
- `com.nsbm.rocs.entity.inventory.GRNItem` - GRN item entity
- `com.nsbm.rocs.entity.inventory.Stock` - Stock entity
- `com.nsbm.rocs.entity.inventory.Batch` - Batch entity
- `com.nsbm.rocs.entity.inventory.Product` - Product entity
- `com.nsbm.rocs.entity.inventory.Supplier` - Supplier entity
- `com.nsbm.rocs.entity.main.Branch` - Branch entity
- `com.nsbm.rocs.entity.main.UserProfile` - User entity

### DTOs
- `GRNCreateRequestDTO` - Create GRN request
- `GRNUpdateRequestDTO` - Update GRN request
- `GRNResponseDTO` - GRN response with full details
- `GRNItemDTO` - GRN item details
- `GRNFilterDTO` - Search filters
- `GRNStatsDTO` - Statistics response

### Repositories
- `GRNRepository` - GRN data access with advanced queries
- `GRNItemRepository` - GRN items data access
- `InventoryStockRepository` - Stock management
- `BatchRepository` - Batch management
- `SupplierRepository` - Supplier data access
- `ProductRepository` - Product data access
- `BranchRepository` - Branch data access
- `UserRepository` - User data access

### Service Layer
- `GRNService` - Service interface
- `GRNServiceImpl` - Complete service implementation with:
  - GRN creation and validation
  - Approval workflow
  - Stock integration
  - Statistics calculation
  - Search and filtering

### Controller
- `GRNController` - REST API endpoints with proper error handling

### Exception Handling
- `GRNException` - Custom exception for GRN operations
- Comprehensive error responses with proper HTTP status codes

### Utilities
- `InventoryUtils` - Helper methods for validation and code generation
- `InventoryResponseBuilder` - Response formatting utility

## Business Logic

### GRN Creation Process
1. Validate supplier exists and is active
2. Generate unique GRN number
3. Create GRN record with PENDING status
4. Validate and create GRN items
5. Calculate totals
6. Return complete GRN details

### Approval Process
1. Validate GRN is in PENDING status
2. Update GRN status to APPROVED
3. Update stock levels for all items
4. Create batch records if batch codes provided
5. Set approver and approval timestamp

### Stock Update Logic
1. Find existing stock record or create new one
2. Add received quantity to total stock
3. Update available quantity
4. Create batch record with expiry tracking

## API Response Format
```json
{
  "success": true,
  "message": "GRN created successfully",
  "data": {
    "grnId": 1,
    "grnNo": "GRN-1-20240205-001",
    "branchName": "Main Branch",
    "supplierName": "ABC Supplier",
    "totalAmount": 1500.00,
    "status": "PENDING",
    "items": [...]
  },
  "timestamp": "2024-02-05T10:30:00"
}
```

## Security Features
- User-based creation tracking
- Approval workflow with user validation
- Operation restrictions based on GRN status
- Audit trail for all changes

## Performance Optimizations
- Efficient database queries with proper indexing
- Batch processing for multiple items
- Lazy loading of related entities
- Pagination support in search operations

## Validation Rules
- Required fields validation
- Business logic validation (status transitions)
- Data integrity checks
- Duplicate prevention

This GRN system provides a complete, production-ready inventory receiving solution with full integration to your existing database schema.
