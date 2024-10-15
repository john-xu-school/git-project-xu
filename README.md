staging: yes it works well by creating a tree on the full working directory everytime
commit: it works by creating a commit file in git with relevant links
checkout: it works well by creating a copy folder under any directory we want (can be the same as the original)
bugs: commit's HEAD pointed to the hash of the root instead of hash of the most recent commit, fixed by changing what's written to HEAD 
