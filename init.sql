create table if not exists agency(
    id serial primary key,
    name text not null,
    corporateName text not null,
    registrationNumber text not null,
    streetAddress text not null,
    city text not null,
    state text not null,
    zipCode text not null,
    country text not null
);
