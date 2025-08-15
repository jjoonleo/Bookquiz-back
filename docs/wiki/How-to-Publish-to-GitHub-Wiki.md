How to Publish to GitHub Wiki

Option A: Manual copy via GitHub UI

1. Go to the repo's Wiki tab on GitHub and create pages matching the files in `docs/wiki`.
2. Copy content from each local file and save.

Option B: Push to the Wiki Git repo
The GitHub Wiki is a separate git repository at `<repo>.wiki.git`.

Steps

```bash
rm -rf wiki-publish \
  && git clone https://github.com/jjoonleo/Bookquiz-back.wiki.git wiki-publish \
  && rsync -av --delete docs/wiki/ wiki-publish/ \
  && cd wiki-publish \
  && git add -A \
  && git commit -m "Publish wiki from docs/wiki" \
  && git push
```

Notes

-   The `--delete` flag removes pages in the wiki that are not present locally (sync behavior). Remove it if you want to preserve existing remote-only pages.
-   Ensure `git` user.name and user.email are configured.
