```sql
DROP DATABASE IF EXISTS smartretailpro; 
CREATE DATABASE smartretailpro   
CHARACTER SET utf8mb4   
COLLATE utf8mb4_unicode_ci;  

USE smartretailpro; 

CREATE TABLE branches (   
    branch_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    name VARCHAR(100) NOT NULL,   
    code VARCHAR(20) UNIQUE NOT NULL,   
    address VARCHAR(255),   
    phone VARCHAR(30),   
    email VARCHAR(120),   
    is_active BOOLEAN DEFAULT TRUE,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 
); 

CREATE TABLE user_profiles(   
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    branch_id BIGINT,      
    -- Credentials   
    username VARCHAR(100) UNIQUE NOT NULL,   
    email VARCHAR(150) UNIQUE NOT NULL,   
    password VARCHAR(255) NOT NULL,      
    -- Profile   
    full_name VARCHAR(150) NOT NULL,   
    phone VARCHAR(30),   
    employee_id VARCHAR(50) UNIQUE,      
    -- Role & Access   
    role VARCHAR(40), -- SUPER_ADMIN, MANAGER, CASHIER, STORE_KEEPER      
    -- Account Status   
    account_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, ACTIVE, INACTIVE, SUSPENDED, REJECTED       
    -- Approval Tracking   
    approved_by BIGINT,   
    approved_at TIMESTAMP NULL,   
    rejection_reason TEXT,      
    -- Security   
    failed_login_attempts INT DEFAULT 0,   
    last_login TIMESTAMP NULL,   
    must_change_password BOOLEAN DEFAULT FALSE,      
    -- Email Verification   
    registration_token VARCHAR(100) UNIQUE,   
    email_verified BOOLEAN DEFAULT FALSE,   
    email_verified_at TIMESTAMP NULL,      
    -- Timestamps   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,      
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id),   
    FOREIGN KEY (approved_by) REFERENCES user_profiles(user_id),   
    INDEX idx_status (account_status),   
    INDEX idx_username (username) 
);  

CREATE TABLE terminals (  
    terminal_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    branch_id BIGINT NOT NULL,  
    terminal_code VARCHAR(50) NULL,  
    is_active TINYINT(1) NOT NULL DEFAULT 1,   
    code VARCHAR(30) NULL,   
    name VARCHAR(100) NULL,   
    created_at DATETIME(6) NULL,   
    updated_at DATETIME(6) NULL,  
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id) 
);  

CREATE TABLE user_approval_requests (   
    request_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    user_id BIGINT NOT NULL,   
    request_type VARCHAR(30) NOT NULL, -- REGISTRATION, ROLE_CHANGE, REACTIVATION   
    requested_branch_id BIGINT,   
    reason TEXT,   
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED   
    reviewed_by BIGINT,   
    reviewed_at TIMESTAMP NULL,   
    reviewer_notes TEXT,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      
    FOREIGN KEY (user_id) REFERENCES user_profiles(user_id) ON DELETE CASCADE,   
    FOREIGN KEY (requested_branch_id) REFERENCES branches(branch_id),   
    FOREIGN KEY (reviewed_by) REFERENCES user_profiles(user_id),   
    INDEX idx_status (status) 
); 

CREATE TABLE user_sessions (   
    session_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    user_id BIGINT NOT NULL,   
    token VARCHAR(255) UNIQUE NOT NULL,   
    ip_address VARCHAR(45),   
    user_agent TEXT,   
    is_active BOOLEAN DEFAULT TRUE,   
    expires_at TIMESTAMP NOT NULL,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,      
    FOREIGN KEY (user_id) REFERENCES user_profiles(user_id) ON DELETE CASCADE,   
    INDEX idx_token (token),   
    INDEX idx_expires (expires_at) 
); 

CREATE TABLE permissions (   
    permission_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    code VARCHAR(50) UNIQUE NOT NULL,   
    name VARCHAR(100) NOT NULL,   
    description TEXT,   
    module VARCHAR(50), -- POS, INVENTORY, PROCUREMENT, ACCOUNTING, REPORTS   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
);		 

CREATE TABLE role_permissions (   
    role VARCHAR(40) NOT NULL,   
    permission_id BIGINT NOT NULL,   
    PRIMARY KEY (role, permission_id),   
    FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE 
); 

CREATE TABLE user_permissions (   
    user_id BIGINT NOT NULL,   
    permission_id BIGINT NOT NULL,   
    granted_by BIGINT,   
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   
    PRIMARY KEY (user_id, permission_id),   
    FOREIGN KEY (user_id) REFERENCES user_profiles(user_id) ON DELETE CASCADE,   
    FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE,   
    FOREIGN KEY (granted_by) REFERENCES user_profiles(user_id) 
); 

CREATE TABLE suppliers (   
    supplier_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    code VARCHAR(30) UNIQUE NOT NULL,   
    name VARCHAR(150) NOT NULL,   
    company_name VARCHAR(200),   
    -- Contact   
    contact_person VARCHAR(100),   
    phone VARCHAR(30),   
    mobile VARCHAR(30),   
    email VARCHAR(120),   
    website VARCHAR(150),   
    -- Address   
    address_line1 VARCHAR(255),   
    address_line2 VARCHAR(255),   
    city VARCHAR(100),   
    state VARCHAR(100),   
    postal_code VARCHAR(20),   
    country VARCHAR(100) DEFAULT 'Sri Lanka',      
    -- Business   
    tax_id VARCHAR(50),   
    vat_number VARCHAR(50),   
    business_registration_no VARCHAR(50),      
    -- Payment Terms   
    credit_days INT DEFAULT 0,   
    credit_limit DECIMAL(15,2) DEFAULT 0.00,   
    payment_terms VARCHAR(50), -- NET_30, NET_60, COD, ADVANCE   
    bank_name VARCHAR(100),   
    bank_account_no VARCHAR(50),   
    bank_branch VARCHAR(100),      
    -- Classification   
    supplier_type VARCHAR(50), -- LOCAL, INTERNATIONAL, DISTRIBUTOR, MANUFACTURER   
    supplier_category VARCHAR(50), -- PRIMARY, SECONDARY, OCCASIONAL   
    rating INT DEFAULT 0,      
    -- Status   
    is_active BOOLEAN DEFAULT TRUE,   
    is_verified BOOLEAN DEFAULT FALSE,   
    blacklisted BOOLEAN DEFAULT FALSE,   
    blacklist_reason TEXT,      
    -- Metadata   
    notes TEXT,   
    created_by BIGINT,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,      
    FOREIGN KEY (created_by) REFERENCES user_profiles(user_id) 
); 

CREATE TABLE supplier_branches (   
    supplier_id BIGINT NOT NULL,   
    branch_id BIGINT NOT NULL,   
    is_preferred BOOLEAN DEFAULT FALSE,   
    discount_percentage DECIMAL(5,2) DEFAULT 0.00,   
    notes TEXT,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   
    PRIMARY KEY (supplier_id, branch_id),   
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE CASCADE,   
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id) ON DELETE CASCADE 
); 

CREATE TABLE supplier_contacts (   
    contact_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    supplier_id BIGINT NOT NULL,   
    name VARCHAR(150) NOT NULL,   
    designation VARCHAR(100),   
    phone VARCHAR(30),   
    mobile VARCHAR(30),   
    email VARCHAR(120),   
    is_primary BOOLEAN DEFAULT FALSE,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE CASCADE 
); 

CREATE TABLE supplier_ledger (   
    ledger_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    supplier_id BIGINT NOT NULL,   
    branch_id BIGINT NOT NULL,   
    transaction_date DATE NOT NULL,   
    transaction_type VARCHAR(30) NOT NULL, -- PURCHASE, PAYMENT, RETURN, DEBIT_NOTE, CREDIT_NOTE   
    reference_no VARCHAR(100),   
    reference_id BIGINT,   
    debit DECIMAL(15,2) DEFAULT 0.00,   
    credit DECIMAL(15,2) DEFAULT 0.00,   
    balance DECIMAL(15,2) DEFAULT 0.00,   
    description TEXT,   
    created_by BIGINT,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id),   
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id),   
    FOREIGN KEY (created_by) REFERENCES user_profiles(user_id),   
    INDEX idx_supplier_date (supplier_id, transaction_date) 
); 

CREATE TABLE supplier_performance (   
    performance_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    supplier_id BIGINT NOT NULL,   
    branch_id BIGINT,   
    evaluation_date DATE NOT NULL,   
    quality_rating INT,   
    delivery_rating INT,   
    price_rating INT,   
    service_rating INT,   
    overall_rating DECIMAL(3,2),   
    comments TEXT,   
    evaluated_by BIGINT,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE CASCADE,   
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id),   
    FOREIGN KEY (evaluated_by) REFERENCES user_profiles(user_id) 
); 

CREATE TABLE categories (   
    category_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    name VARCHAR(100) NOT NULL,   
    description TEXT,   
    is_active BOOLEAN DEFAULT TRUE,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
);  

CREATE TABLE subcategories (   
    subcategory_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    category_id BIGINT NOT NULL,   
    name VARCHAR(100) NOT NULL,   
    description TEXT,   
    is_active BOOLEAN DEFAULT TRUE,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE CASCADE 
);  

CREATE TABLE brands (   
    brand_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    name VARCHAR(100) NOT NULL,   
    description TEXT,   
    is_active BOOLEAN DEFAULT TRUE,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
);  

CREATE TABLE units (   
    unit_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    name VARCHAR(50) NOT NULL,   
    symbol VARCHAR(10),   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
); 

CREATE TABLE products (   
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    sku VARCHAR(60) UNIQUE NOT NULL,   
    barcode VARCHAR(60) UNIQUE,   
    name VARCHAR(150) NOT NULL,   
    description TEXT,   
    category_id BIGINT,   
    subcategory_id BIGINT,   
    brand_id BIGINT,   
    unit_id BIGINT,      
    -- Pricing   
    cost_price DECIMAL(12,2) DEFAULT 0.00,   
    selling_price DECIMAL(12,2) DEFAULT 0.00,   
    mrp DECIMAL(12,2) DEFAULT 0.00,      
    -- Stock Management   
    reorder_level DECIMAL(12,3) DEFAULT 0.00,  -- Updated to DECIMAL
    max_stock_level DECIMAL(12,3) DEFAULT 0.00, -- Updated to DECIMAL
    is_serialized BOOLEAN DEFAULT FALSE,      
    -- Tax & Warranty   
    tax_rate DECIMAL(5,2) DEFAULT 0.00,   
    warranty_months INT DEFAULT 0,      
    -- Status   
    is_active BOOLEAN DEFAULT TRUE,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,      
    FOREIGN KEY (category_id) REFERENCES categories(category_id),   
    FOREIGN KEY (subcategory_id) REFERENCES subcategories(subcategory_id),   
    FOREIGN KEY (brand_id) REFERENCES brands(brand_id),   
    FOREIGN KEY (unit_id) REFERENCES units(unit_id) 
); 

CREATE TABLE supplier_products (   
    supplier_product_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    supplier_id BIGINT NOT NULL,   
    product_id BIGINT NOT NULL,   
    supplier_sku VARCHAR(100),   
    cost_price DECIMAL(12,2) NOT NULL,   
    moq INT DEFAULT 1,   
    lead_time_days INT DEFAULT 0,   
    is_preferred BOOLEAN DEFAULT FALSE,   
    last_purchase_date DATE,   
    last_purchase_price DECIMAL(12,2),   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,      
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE CASCADE,   
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,   
    UNIQUE KEY unique_supplier_product (supplier_id, product_id) 
); 

CREATE TABLE stock (   
    stock_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    branch_id BIGINT NOT NULL,   
    product_id BIGINT NOT NULL,   
    quantity DECIMAL(12,3) DEFAULT 0.000, -- Updated to DECIMAL
    reserved_qty DECIMAL(12,3) DEFAULT 0.000, -- Updated to DECIMAL
    available_qty DECIMAL(12,3) AS (quantity - reserved_qty) STORED,   
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,      
    UNIQUE KEY unique_branch_product (branch_id, product_id),   
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id) ON DELETE CASCADE,   
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE 
); 

CREATE TABLE batches (   
    batch_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    product_id BIGINT NOT NULL,   
    branch_id BIGINT NOT NULL,   
    batch_code VARCHAR(60) NOT NULL,   
    manufacturing_date DATE,   
    expiry_date DATE,   
    qty DECIMAL(12,3) DEFAULT 0.000, -- Updated to DECIMAL
    cost_price DECIMAL(12,2),   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,   
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id) ON DELETE CASCADE,   
    INDEX idx_expiry (expiry_date) 
); 

CREATE TABLE product_serials (   
    serial_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    product_id BIGINT NOT NULL,   
    branch_id BIGINT NOT NULL,   
    serial_no VARCHAR(100) UNIQUE NOT NULL,   
    barcode VARCHAR(60) UNIQUE,   
    batch_id BIGINT,   
    status VARCHAR(20) DEFAULT 'IN_STOCK', -- IN_STOCK, SOLD, DAMAGED, RETURNED   
    grn_id BIGINT,   
    sale_id BIGINT,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   
    sold_at TIMESTAMP NULL,      
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,   
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id) ON DELETE CASCADE,   
    FOREIGN KEY (batch_id) REFERENCES batches(batch_id),   
    INDEX idx_status (status) 
); 

CREATE TABLE purchase_orders (   
    po_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    po_no VARCHAR(50) UNIQUE NOT NULL,   
    branch_id BIGINT NOT NULL,   
    supplier_id BIGINT NOT NULL,   
    po_date DATE NOT NULL,   
    expected_delivery_date DATE,   
    payment_terms VARCHAR(50),   
    total_amount DECIMAL(15,2) DEFAULT 0.00,   
    tax_amount DECIMAL(12,2) DEFAULT 0.00,   
    discount_amount DECIMAL(12,2) DEFAULT 0.00,   
    net_amount DECIMAL(15,2) DEFAULT 0.00,   
    status VARCHAR(30) DEFAULT 'DRAFT', -- DRAFT, PENDING, APPROVED, PARTIALLY_RECEIVED, RECEIVED, CANCELLED   
    notes TEXT,   
    created_by BIGINT,   
    approved_by BIGINT,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,      
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id),   
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id),   
    FOREIGN KEY (created_by) REFERENCES user_profiles(user_id),   
    FOREIGN KEY (approved_by) REFERENCES user_profiles(user_id) 
);  

CREATE TABLE purchase_order_items (   
    po_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    po_id BIGINT NOT NULL,   
    product_id BIGINT NOT NULL,   
    qty_ordered DECIMAL(12,3) NOT NULL, -- Updated to DECIMAL
    qty_received DECIMAL(12,3) DEFAULT 0.000, -- Updated to DECIMAL
    unit_price DECIMAL(12,2) NOT NULL,   
    tax_rate DECIMAL(5,2) DEFAULT 0.00,   
    discount DECIMAL(10,2) DEFAULT 0.00,   
    total DECIMAL(12,2),      
    FOREIGN KEY (po_id) REFERENCES purchase_orders(po_id) ON DELETE CASCADE,   
    FOREIGN KEY (product_id) REFERENCES products(product_id) 
); 

CREATE TABLE grns (   
    grn_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    grn_no VARCHAR(50) UNIQUE NOT NULL,   
    branch_id BIGINT NOT NULL,   
    supplier_id BIGINT NOT NULL,   
    po_id BIGINT,   
    grn_date DATE NOT NULL,   
    invoice_no VARCHAR(100),   
    invoice_date DATE,   
    total_amount DECIMAL(15,2) DEFAULT 0.00,   
    tax_amount DECIMAL(12,2) DEFAULT 0.00,   
    discount_amount DECIMAL(12,2) DEFAULT 0.00,   
    net_amount DECIMAL(15,2) DEFAULT 0.00,   
    payment_status VARCHAR(30) DEFAULT 'UNPAID', -- UNPAID, PARTIALLY_PAID, PAID   
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED   
    notes TEXT,   
    created_by BIGINT,   
    approved_by BIGINT,   
    approved_at TIMESTAMP NULL,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,      
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id),   
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id),   
    FOREIGN KEY (po_id) REFERENCES purchase_orders(po_id),   
    FOREIGN KEY (created_by) REFERENCES user_profiles(user_id),   
    FOREIGN KEY (approved_by) REFERENCES user_profiles(user_id) 
);  

CREATE TABLE grn_items (   
    grn_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    grn_id BIGINT NOT NULL,   
    product_id BIGINT NOT NULL,   
    batch_code VARCHAR(60),   
    expiry_date DATE,   
    qty_received DECIMAL(12,3) NOT NULL, -- Updated to DECIMAL
    unit_price DECIMAL(12,2) NOT NULL,   
    tax_rate DECIMAL(5,2) DEFAULT 0.00,   
    discount DECIMAL(10,2) DEFAULT 0.00,   
    total DECIMAL(12,2),      
    FOREIGN KEY (grn_id) REFERENCES grns(grn_id) ON DELETE CASCADE,   
    FOREIGN KEY (product_id) REFERENCES products(product_id) 
); 

CREATE TABLE supplier_payments (   
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    payment_no VARCHAR(50) UNIQUE NOT NULL,   
    supplier_id BIGINT NOT NULL,   
    branch_id BIGINT NOT NULL,   
    payment_date DATE NOT NULL,   
    payment_method VARCHAR(30), -- CASH, CHEQUE, BANK_TRANSFER, CARD   
    amount DECIMAL(15,2) NOT NULL,   
    reference_no VARCHAR(100),   
    notes TEXT,   
    created_by BIGINT,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id),   
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id),   
    FOREIGN KEY (created_by) REFERENCES user_profiles(user_id) 
);  

CREATE TABLE supplier_payment_allocations (   
    allocation_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    payment_id BIGINT NOT NULL,   
    grn_id BIGINT NOT NULL,   
    amount DECIMAL(15,2) NOT NULL,      
    FOREIGN KEY (payment_id) REFERENCES supplier_payments(payment_id) ON DELETE CASCADE,   
    FOREIGN KEY (grn_id) REFERENCES grns(grn_id) 
); 

CREATE TABLE customers (   
    customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    code VARCHAR(30) UNIQUE,   
    name VARCHAR(150) NOT NULL,   
    phone VARCHAR(20) UNIQUE, -- Updated to UNIQUE
    email VARCHAR(120),   
    address VARCHAR(255),   
    city VARCHAR(100),   
    date_of_birth DATE,   
    loyalty_points INT DEFAULT 0,   
    total_purchases DECIMAL(15,2) DEFAULT 0.00,   
    is_active BOOLEAN DEFAULT TRUE,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 
);  

CREATE TABLE cash_shifts (   
    shift_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    shift_no VARCHAR(30) UNIQUE,    
    branch_id BIGINT NOT NULL,   
    terminal_id BIGINT NOT NULL,   
    cashier_id BIGINT NOT NULL,    
    opened_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,   
    closed_at TIMESTAMP NULL,    
    opening_cash DECIMAL(12,2) NOT NULL DEFAULT 0.00,   
    closing_cash DECIMAL(12,2) DEFAULT 0.00,   
    expected_cash DECIMAL(12,2) DEFAULT 0.00,   
    cash_difference DECIMAL(12,2) DEFAULT 0.00,    
    total_sales DECIMAL(15,2) DEFAULT 0.00,   
    total_returns DECIMAL(15,2) DEFAULT 0.00,    
    approved_by BIGINT NULL,   
    approved_at TIMESTAMP NULL,    
    status ENUM('OPEN','CLOSED','SUSPENDED') DEFAULT 'OPEN',   
    notes TEXT,    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,    
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id),   
    FOREIGN KEY (terminal_id) REFERENCES terminals(terminal_id),   
    FOREIGN KEY (cashier_id) REFERENCES user_profiles(user_id),   
    FOREIGN KEY (approved_by) REFERENCES user_profiles(user_id) 
);  

CREATE TABLE cash_shift_denominations (   
    denom_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    shift_id BIGINT NOT NULL,    
    denomination_value DECIMAL(10,2) NOT NULL,   
    quantity INT NOT NULL,    
    amount DECIMAL(12,2) GENERATED ALWAYS AS      (denomination_value * quantity) STORED,    
    type ENUM('OPENING','CLOSING') NOT NULL,    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,    
    FOREIGN KEY (shift_id) REFERENCES cash_shifts(shift_id)     ON DELETE CASCADE 
); 

CREATE TABLE cash_flows (   
    flow_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    amount DECIMAL(12,2) NOT NULL, -- Matched entity nullable=false
    created_at DATETIME(6) NULL,   
    created_by BIGINT NULL,   
    type VARCHAR(30) NOT NULL, -- Updated from Enum to VARCHAR to match entity behavior
    reason VARCHAR(255) NULL,   
    reference_no VARCHAR(50) NULL,
    shift_id BIGINT NULL,      
    FOREIGN KEY (created_by) REFERENCES user_profiles(user_id),   
    FOREIGN KEY (shift_id) REFERENCES cash_shifts(shift_id) 
);    

CREATE TABLE sales (   
    sale_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    invoice_no VARCHAR(50) UNIQUE NOT NULL,   
    branch_id BIGINT NOT NULL,   
    cashier_id BIGINT NOT NULL,   
    customer_id BIGINT,   
    shift_id BIGINT,   
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   
    gross_total DECIMAL(12,2) DEFAULT 0.00,   
    discount DECIMAL(10,2) DEFAULT 0.00,   
    tax_amount DECIMAL(10,2) DEFAULT 0.00,   
    net_total DECIMAL(12,2) DEFAULT 0.00,   
    paid_amount DECIMAL(12,2) DEFAULT 0.00,   
    change_amount DECIMAL(10,2) DEFAULT 0.00,   
    payment_status VARCHAR(20) DEFAULT 'PAID', -- PAID, PARTIAL, PENDING   
    sale_type VARCHAR(20) DEFAULT 'RETAIL', -- RETAIL, WHOLESALE   
    notes TEXT,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id),   
    FOREIGN KEY (cashier_id) REFERENCES user_profiles(user_id),   
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),   
    FOREIGN KEY (shift_id) REFERENCES cash_shifts(shift_id) 
);  

CREATE TABLE sale_items (   
    sale_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    sale_id BIGINT NOT NULL,   
    product_id BIGINT NOT NULL,   
    serial_id BIGINT,   
    qty DECIMAL(12,3) NOT NULL, -- Updated to DECIMAL
    unit_price DECIMAL(10,2) NOT NULL,   
    discount DECIMAL(10,2) DEFAULT 0.00,   
    tax_rate DECIMAL(5,2) DEFAULT 0.00,   
    total DECIMAL(10,2),      
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id) ON DELETE CASCADE,   
    FOREIGN KEY (product_id) REFERENCES products(product_id),   
    FOREIGN KEY (serial_id) REFERENCES product_serials(serial_id) 
);  

CREATE TABLE payments (   
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    sale_id BIGINT NOT NULL,   
    payment_type VARCHAR(30) NOT NULL, -- CASH, CARD, QR, BANK_TRANSFER   
    amount DECIMAL(10,2) NOT NULL,   
    reference_no VARCHAR(100),   
    card_last4 VARCHAR(4),
    bank_name VARCHAR(100), -- Added banking field
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id) ON DELETE CASCADE 
); 

CREATE TABLE sales_returns (   
    return_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    return_no VARCHAR(50) UNIQUE NOT NULL,   
    sale_id BIGINT NOT NULL,   
    branch_id BIGINT NOT NULL,   
    return_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   
    total_amount DECIMAL(12,2),   
    refund_method VARCHAR(30), -- CASH, CARD, CREDIT_NOTE   
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED   
    reason TEXT,   
    approved_by BIGINT,   
    created_by BIGINT,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id),   
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id),   
    FOREIGN KEY (approved_by) REFERENCES user_profiles(user_id),   
    FOREIGN KEY (created_by) REFERENCES user_profiles(user_id) 
);  

CREATE TABLE sales_return_items (   
    return_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    return_id BIGINT NOT NULL,   
    sale_item_id BIGINT NOT NULL,   
    product_id BIGINT NOT NULL,   
    serial_id BIGINT,   
    qty DECIMAL(12,3) NOT NULL, -- Updated to DECIMAL
    unit_price DECIMAL(10,2),   
    total DECIMAL(10,2),      
    FOREIGN KEY (return_id) REFERENCES sales_returns(return_id) ON DELETE CASCADE,   
    FOREIGN KEY (sale_item_id) REFERENCES sale_items(sale_item_id),   
    FOREIGN KEY (product_id) REFERENCES products(product_id),   
    FOREIGN KEY (serial_id) REFERENCES product_serials(serial_id) 
); 

CREATE TABLE approvals (   
    approval_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    branch_id BIGINT NOT NULL,   
    type VARCHAR(50) NOT NULL, -- DISCOUNT, RETURN, GRN, STOCK_ADJUST, PAYMENT, PRICE_CHANGE   
    reference_id BIGINT,   
    reference_no VARCHAR(100),   
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED   
    requested_by BIGINT NOT NULL,   
    approved_by BIGINT,   
    request_notes TEXT,   
    approval_notes TEXT,   
    payload_json TEXT,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   
    approved_at TIMESTAMP NULL,      
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id),   
    FOREIGN KEY (requested_by) REFERENCES user_profiles(user_id),   
    FOREIGN KEY (approved_by) REFERENCES user_profiles(user_id),   
    INDEX idx_status (status),   
    INDEX idx_type (type) 
); 

CREATE TABLE audit_logs (   
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    user_id BIGINT,   
    branch_id BIGINT,   
    action VARCHAR(50) NOT NULL, -- CREATE, UPDATE, DELETE, LOGIN, LOGOUT, APPROVE, REJECT   
    table_name VARCHAR(100),   
    record_id BIGINT,   
    old_values TEXT,   
    new_values TEXT,   
    ip_address VARCHAR(45),   
    user_agent TEXT,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      
    FOREIGN KEY (user_id) REFERENCES user_profiles(user_id) ON DELETE SET NULL,   
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id),   
    INDEX idx_user_date (user_id, created_at),   
    INDEX idx_table (table_name, record_id) 
); 

CREATE TABLE user_activity_log (   
    activity_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    user_id BIGINT,   
    activity_type VARCHAR(50) NOT NULL, -- LOGIN_SUCCESS, LOGIN_FAILED, LOGOUT, PASSWORD_CHANGE, etc.   
    description TEXT,   
    ip_address VARCHAR(45),   
    user_agent TEXT,   
    branch_id BIGINT,   
    performed_by BIGINT,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      
    FOREIGN KEY (user_id) REFERENCES user_profiles(user_id) ON DELETE SET NULL,   
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id),   
    FOREIGN KEY (performed_by) REFERENCES user_profiles(user_id),   
    INDEX idx_user_date (user_id, created_at),   
    INDEX idx_activity_type (activity_type) 
); 

CREATE TABLE accounts (   
    account_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    code VARCHAR(20) UNIQUE NOT NULL,   
    name VARCHAR(120) NOT NULL,   
    type VARCHAR(20) NOT NULL, -- ASSET, LIABILITY, EQUITY, INCOME, EXPENSE   
    parent_id BIGINT,   
    is_active BOOLEAN DEFAULT TRUE,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      
    FOREIGN KEY (parent_id) REFERENCES accounts(account_id) 
); 

CREATE TABLE journal_entries (   
    journal_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    journal_no VARCHAR(50) UNIQUE NOT NULL,   
    branch_id BIGINT NOT NULL,   
    entry_date DATE NOT NULL,   
    transaction_type VARCHAR(50), -- SALE, PURCHASE, PAYMENT, RECEIPT, ADJUSTMENT   
    reference_no VARCHAR(100),   
    memo VARCHAR(255),   
    total_debit DECIMAL(15,2) DEFAULT 0.00,   
    total_credit DECIMAL(15,2) DEFAULT 0.00,   
    status VARCHAR(20) DEFAULT 'DRAFT', -- DRAFT, POSTED, VOID   
    created_by BIGINT,   
    posted_by BIGINT,   
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   
    posted_at TIMESTAMP NULL,      
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id),   
    FOREIGN KEY (created_by) REFERENCES user_profiles(user_id),   
    FOREIGN KEY (posted_by) REFERENCES user_profiles(user_id) 
);  

CREATE TABLE journal_lines (   
    line_id BIGINT PRIMARY KEY AUTO_INCREMENT,   
    journal_id BIGINT NOT NULL,   
    account_id BIGINT NOT NULL,   
    debit DECIMAL(12,2) DEFAULT 0.00,   
    credit DECIMAL(12,2) DEFAULT 0.00,   
    description VARCHAR(255),      
    FOREIGN KEY (journal_id) REFERENCES journal_entries(journal_id) ON DELETE CASCADE,   
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) 
);
```
