the original file `wiki-news-300d-1M.vec.zip` was downloaded from:
https://fasttext.cc/docs/en/english-vectors.html#content

then, we ran the following to generate `examples.vec`:

```bash
zcat wiki-news-300d-1M.vec.zip | awk '$1=="female" || $1=="male" || $1=="queen" || $1=="king" || $1=="programming" || $1=="data" || $1=="bike" || $1=="bycicle"' > examples.vec
```
