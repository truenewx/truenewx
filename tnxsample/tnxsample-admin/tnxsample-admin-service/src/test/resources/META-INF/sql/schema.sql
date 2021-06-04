create table t_manager (
    id int not null auto_increment,
    job_no varchar(32),
    username varchar(20) not null,
    password char(64),
    head_image_url varchar(80),
    full_name varchar(20) not null,
    index_name varchar(50),
    top bit(1) default 0 not null,
    disabled bit(1) default 0 not null,
    create_time timestamp not null default current_timestamp,

    constraint pk_manager primary key (id),
    constraint uk_manager_username unique key (username)
);

create table t_role (
    id int not null auto_increment,
    role_name varchar(20) not null,
    remark varchar(200),
    ordinal bigint not null,
    permission_string varchar(4000),

    constraint pk_role primary key (id)
);

create table t_manager_role_relation (
    manager_id int not null,
    role_id int not null,

    constraint pk_manager_role_relation primary key (manager_id, role_id),

    constraint fk_manager_role_relation_manager_id foreign key (manager_id) references t_manager (id),
    constraint fk_manager_role_relation_role_id foreign key (role_id) references t_role (id)
);
