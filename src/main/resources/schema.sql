create table course (
    id bigint generated by default as identity,
    title varchar(255) not null,
    creator_id bigint not null,
    created_at timestamp not null,
    updated_at timestamp,
    primary key (id)
);

create table ns_user (
    id bigint generated by default as identity,
    user_id varchar(20) not null,
    password varchar(20) not null,
    name varchar(20) not null,
    email varchar(50),
    created_at timestamp not null,
    updated_at timestamp,
    primary key (id)
);

create table question (
    id bigint generated by default as identity,
    created_at timestamp not null,
    updated_at timestamp,
    contents clob,
    deleted boolean not null,
    title varchar(100) not null,
    writer_id bigint,
    primary key (id)
);

create table answer (
    id bigint generated by default as identity,
    created_at timestamp not null,
    updated_at timestamp,
    contents clob,
    deleted boolean not null,
    question_id bigint,
    writer_id bigint,
    primary key (id)
);

create table delete_history (
    id bigint not null,
    content_id bigint,
    content_type varchar(255),
    created_date timestamp,
    deleted_by_id bigint,
    primary key (id)
);

create table session(
    id bigint generated by default as identity,
    title varchar(255) not null,
    image text,
    start_date timestamp,
    end_date timestamp,
    course_id bigint,
    state enum('ready', 'recruiting', 'end'),
    type enum('paid', 'free'),
    primary key (id)
);

create table enrollment(
    id bigint generated by default as identity,
    student_capacity int(4),
    fee int(4),
    session_id bigint,
    created_at timestamp not null,
    updated_at timestamp,
    primary key (id)
)

create table enrollment_student(
    id bigint generated by default as identity,
    enrollment_id bigint,
    student_id bigint,
    primary key (id)
)
