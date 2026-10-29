create table "order" (
     id bigint not null,
     customer_id uuid not null,
     status varchar(255),
     total_amount numeric(38, 2),
     total_items integer,
     payment_method varchar(255),
     placed_at timestamp with time zone,
     paid_at timestamp with time zone,
     ready_at timestamp with time zone,
     canceled_at timestamp with time zone,
     shipping_city varchar(255),
     shipping_state varchar(255),
     constraint pk_order primary key (id)
);

create index idx_order_customer_id on "order" (customer_id);
create index idx_order_paid_at on "order" (paid_at);