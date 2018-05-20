

val a, b, c = Option(1)

val r =
  for {
    i <- a
    j <- b
    k <- c
  } yield i + j + k

