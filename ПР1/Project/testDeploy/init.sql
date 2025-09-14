CREATE TABLE Persons(
                        id serial primary key ,
                        login varchar not null ,
                        password varchar not null,
                        nickname varchar not null,
                        role varchar not null
);
CREATE TABLE Formulas(
                         id serial primary key ,
                         Person_id int references Persons(id),
                         formula json not null
)