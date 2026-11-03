create table product (
     id uuid not null,
     name varchar(255),
     brand varchar(255),
     regular_price numeric(38, 2),
     sale_price numeric(38, 2),
     quantity_in_stock integer,
     enabled boolean,
     category_id uuid,
     category_name varchar(255),
     added_at timestamp with time zone,
     updated_at timestamp with time zone,
     constraint pk_product primary key (id)
);

create index idx_product_category_id on product (category_id);