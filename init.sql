create table if not exists p_ai
(
    hidden                boolean not null,
    created_at            timestamp(6),
    deleted_at            timestamp(6),
    updated_at            timestamp(6),
    id                    uuid    not null
        primary key,
    store_id              uuid,
    question              text    not null,
    answer                text    not null,
    created_by            varchar(255),
    deleted_by            varchar(255),
    description_hint      text,
    food_name             text    not null,
    generated_description text,
    keyword               text,
    updated_by            varchar(255)
);

comment on column p_ai.created_at is '생성일';

comment on column p_ai.deleted_at is '삭제일';

comment on column p_ai.updated_at is '수정일';

comment on column p_ai.created_by is '생성자';

comment on column p_ai.deleted_by is '삭제자';

comment on column p_ai.updated_by is '수정자';

alter table p_ai
    owner to postgres;

create table if not exists p_category
(
    hidden      boolean      not null,
    created_at  timestamp(6),
    deleted_at  timestamp(6),
    updated_at  timestamp(6),
    id          uuid         not null
        primary key,
    created_by  varchar(255),
    deleted_by  varchar(255),
    description varchar(255),
    name        varchar(255) not null
        unique,
    updated_by  varchar(255)
);

comment on column p_category.created_at is '생성일';

comment on column p_category.deleted_at is '삭제일';

comment on column p_category.updated_at is '수정일';

comment on column p_category.created_by is '생성자';

comment on column p_category.deleted_by is '삭제자';

comment on column p_category.updated_by is '수정자';

alter table p_category
    owner to postgres;

create table if not exists p_refreshtoken
(
    expiry_date   timestamp(6) not null,
    id            bigint generated by default as identity
        primary key,
    refresh_token varchar(512) not null,
    username      varchar(255) not null
        unique
);

alter table p_refreshtoken
    owner to postgres;

create table if not exists p_reviews
(
    hidden     boolean      not null,
    score      integer      not null,
    created_at timestamp(6),
    deleted_at timestamp(6),
    updated_at timestamp(6),
    user_id    bigint       not null,
    order_id   uuid         not null,
    review_id  uuid         not null
        primary key,
    store_id   uuid         not null,
    content    text         not null,
    created_by varchar(255),
    deleted_by varchar(255),
    updated_by varchar(255),
    user_name  varchar(255) not null
);

comment on column p_reviews.created_at is '생성일';

comment on column p_reviews.deleted_at is '삭제일';

comment on column p_reviews.updated_at is '수정일';

comment on column p_reviews.created_by is '생성자';

comment on column p_reviews.deleted_by is '삭제자';

comment on column p_reviews.updated_by is '수정자';

alter table p_reviews
    owner to postgres;

create table if not exists p_review_images
(
    created_at      timestamp(6),
    deleted_at      timestamp(6),
    updated_at      timestamp(6),
    review_id       uuid         not null
        constraint fks7u8etlbregohgwifvfa0mtrn
        references p_reviews,
    review_image_id uuid         not null
        primary key,
    created_by      varchar(255),
    deleted_by      varchar(255),
    updated_by      varchar(255),
    url             varchar(255) not null
);

comment on column p_review_images.created_at is '생성일';

comment on column p_review_images.deleted_at is '삭제일';

comment on column p_review_images.updated_at is '수정일';

comment on column p_review_images.created_by is '생성자';

comment on column p_review_images.deleted_by is '삭제자';

comment on column p_review_images.updated_by is '수정자';

alter table p_review_images
    owner to postgres;

create table if not exists p_users
(
    is_deleted boolean      not null,
    id         bigint generated by default as identity
        primary key,
    email      varchar(255) not null
        unique,
    password   varchar(255) not null,
    phone      varchar(255) not null,
    role       varchar(255) not null
        constraint p_users_role_check
        check ((role)::text = ANY
        ((ARRAY ['CUSTOMER'::character varying, 'OWNER'::character varying, 'MANAGER'::character varying, 'MASTER'::character varying])::text[])),
    username   varchar(255) not null
        unique
);

alter table p_users
    owner to postgres;

create table if not exists p_addresses
(
    is_default boolean      not null,
    is_deleted boolean      not null,
    created_at timestamp(6),
    deleted_at timestamp(6),
    updated_at timestamp(6),
    user_id    bigint       not null
        constraint fk8hn2yn0njrhv60bsovwshmktq
        references p_users,
    id         uuid         not null
        primary key,
    address    varchar(255) not null,
    created_by varchar(255),
    deleted_by varchar(255),
    updated_by varchar(255)
);

comment on column p_addresses.created_at is '생성일';

comment on column p_addresses.deleted_at is '삭제일';

comment on column p_addresses.updated_at is '수정일';

comment on column p_addresses.created_by is '생성자';

comment on column p_addresses.deleted_by is '삭제자';

comment on column p_addresses.updated_by is '수정자';

alter table p_addresses
    owner to postgres;

create table if not exists p_orders
(
    hidden      boolean      not null,
    created_at  timestamp(6),
    deleted_at  timestamp(6),
    total_price bigint,
    updated_at  timestamp(6),
    user_id     bigint       not null
        constraint fknuk7fpk7l1wn5doo4wy5g1vow
        references p_users,
    address_id  uuid         not null,
    order_id    uuid         not null
        primary key,
    review_id   uuid,
    store_id    uuid         not null,
    created_by  varchar(255),
    deleted_by  varchar(255),
    phone       varchar(255) not null,
    request     text,
    status      varchar(255) not null
        constraint p_orders_status_check
        check ((status)::text = ANY
        ((ARRAY ['PENDING'::character varying, 'CONFIRMED'::character varying, 'SHIPPED'::character varying, 'DELIVERED'::character varying, 'CANCELED'::character varying])::text[])),
    store_name  varchar(255) not null,
    updated_by  varchar(255)
);

comment on column p_orders.created_at is '생성일';

comment on column p_orders.deleted_at is '삭제일';

comment on column p_orders.updated_at is '수정일';

comment on column p_orders.created_by is '생성자';

comment on column p_orders.deleted_by is '삭제자';

comment on column p_orders.updated_by is '수정자';

alter table p_orders
    owner to postgres;

create table if not exists p_order_items
(
    quantity      integer      not null,
    created_at    timestamp(6),
    deleted_at    timestamp(6),
    price         bigint       not null,
    updated_at    timestamp(6),
    food_id       uuid         not null,
    order_id      uuid         not null
        constraint fkquswcn84hdunm64xwjbrnc3mc
        references p_orders,
    order_item_id uuid         not null
        primary key,
    created_by    varchar(255),
    deleted_by    varchar(255),
    food_name     varchar(255) not null,
    updated_by    varchar(255)
);

comment on column p_order_items.created_at is '생성일';

comment on column p_order_items.deleted_at is '삭제일';

comment on column p_order_items.updated_at is '수정일';

comment on column p_order_items.created_by is '생성자';

comment on column p_order_items.deleted_by is '삭제자';

comment on column p_order_items.updated_by is '수정자';

alter table p_order_items
    owner to postgres;

create table if not exists p_store
(
    hidden      boolean      not null,
    created_at  timestamp(6),
    deleted_at  timestamp(6),
    updated_at  timestamp(6),
    user_id     bigint       not null
        constraint fkt0cqa7pyqclhoh8o7nt15a2bm
        references p_users,
    category_id uuid         not null
        constraint fktp532tgwu3ue2geba9c80nkqh
        references p_category,
    id          uuid         not null
        primary key,
    address     varchar(255) not null,
    close_time  varchar(255) not null,
    created_by  varchar(255),
    deleted_by  varchar(255),
    name        varchar(255) not null,
    open_time   varchar(255) not null,
    phone       varchar(255) not null,
    updated_by  varchar(255)
);

comment on column p_store.created_at is '생성일';

comment on column p_store.deleted_at is '삭제일';

comment on column p_store.updated_at is '수정일';

comment on column p_store.created_by is '생성자';

comment on column p_store.deleted_by is '삭제자';

comment on column p_store.updated_by is '수정자';

alter table p_store
    owner to postgres;

create table if not exists p_food
(
    hidden      boolean      not null,
    created_at  timestamp(6),
    deleted_at  timestamp(6),
    price       bigint       not null,
    updated_at  timestamp(6),
    user_id     bigint       not null,
    id          uuid         not null
        primary key,
    store_id    uuid         not null
        constraint fkm1q1pcn7l627no5pkm1bocupy
        references p_store,
    created_by  varchar(255),
    deleted_by  varchar(255),
    description varchar(255),
    image       varchar(255),
    name        varchar(255) not null,
    updated_by  varchar(255)
);

comment on column p_food.created_at is '생성일';

comment on column p_food.deleted_at is '삭제일';

comment on column p_food.updated_at is '수정일';

comment on column p_food.created_by is '생성자';

comment on column p_food.deleted_by is '삭제자';

comment on column p_food.updated_by is '수정자';

alter table p_food
    owner to postgres;

create table if not exists p_payment
(
    is_deleted  boolean      not null,
    created_at  timestamp(6),
    deleted_at  timestamp(6),
    updated_at  timestamp(6),
    user_id     bigint       not null,
    id          uuid         not null
        primary key,
    order_id    uuid         not null
        unique
        constraint fksgwuk2y1o9bylngvfcb2xbcps
        references p_orders,
    payment_key uuid         not null
        unique,
    store_id    uuid
        constraint fklibalaqayrexygxl1en9q99gv
        references p_store,
    created_by  varchar(255),
    deleted_by  varchar(255),
    method      varchar(255) not null
        constraint p_payment_method_check
        check ((method)::text = ANY ((ARRAY ['CARD'::character varying, 'CHECK'::character varying])::text[])),
    status      varchar(255) not null
        constraint p_payment_status_check
        check ((status)::text = ANY ((ARRAY ['FAIL'::character varying, 'SUCCESS'::character varying])::text[])),
    updated_by  varchar(255)
);

comment on column p_payment.created_at is '생성일';

comment on column p_payment.deleted_at is '삭제일';

comment on column p_payment.updated_at is '수정일';

comment on column p_payment.created_by is '생성자';

comment on column p_payment.deleted_by is '삭제자';

comment on column p_payment.updated_by is '수정자';

alter table p_payment
    owner to postgres;

