CREATE TABLE review (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  rating INTEGER NOT NULL,
  created_at TIMESTAMP NOT NULL,
  book_id BIGINT REFERENCES book,
  user_id BIGINT REFERENCES _user
);
