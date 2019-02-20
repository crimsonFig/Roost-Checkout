# Instructions for coordinating commits, branches, and pull requests

## writing commits
When creating a commit, using the following template will help convey better understanding of whats happening.

```
(<type>) <short subject>
* <detailed first activity of subject>
* <detailed second activity of subject>
<footer or references to any related commits>
```
### Types
* **enhancement** or **feat**: A new feature
* **fix**: A bug fix
* **docs**: Documentation only changes
* **style**: Changes that do not affect the meaning of the code (white-space, formatting, missing
  semi-colons, etc)
* **refactor**: A code change that neither fixes a bug nor adds a feature
* **perf**: A code change that improves performance
* **test**: Adding missing or correcting existing tests
* **chore**: Changes to the build process or auxiliary tools and libraries such as documentation
  generation
  
## branches
you should almost never commit new code to master. Branches should ideally be made when establishing a feature, refactoring the code, or fixing a bug

For example, a feature would create a new branch labeled as `(feat) <goal`, a refactor branch labeled as `(refactor) <goal>`, and a bugfix as `(fix) <goal>`.


### merging vs rebasing
When needing to update a branch due to commits on master since branching, it is more ideal to `rebase` the branch (onto master) instead of merging. doing so will keep the history of commits more linear.
When ending a branch and combining the branch with master, `merge` the branch (onto master) is more ideal, and then simply delete the branch once merge is complete.

### pull requests
Pull requests are made to get a branch approved and added to the master branch. This should only be done when the branch has been both unit tested and integration tested without failure, and introduces no new bugs.

Ideally, one should invite some other member of the team to essentially peer review the work _**thoroughly**_ before giving it approval.

Optionally, for the sake of organization, one may additionally apply type labels to a pull request for at-a-glance understanding
