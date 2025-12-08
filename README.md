***MiniGit*** is a lightweight, educational version control system implemented in Java.  
it mimics core Git functionality - track, commit, restore, diff, history, while focusing on modular domain-driven architecture.  
The project is an intentionally simple single threaded app at hand engineered with professional architectural patterns.  
***Key Features***  
    - Track changes in a working repository  
    - Commit project state  
    - Restore to a commit  
    - List history of commits for a repository  
    - Crash safe recovery after all incomplete requests  
    - Caching architecture  
    - CLI interface  
    - Pluggable functionality for easy extensibility  
***Essential Architectural Features***
miniGit  
 |--app  
 |  |--state (after request recovery)  
 |  |--usecases (commit, diff, undo, etc..)  
 |  |--validations (validates parameters, user input)    
 |--domain  
 |  |--entities (crucial MiniGitRepository entity - encompasses all DI classes)  
 |  |--services (core interfaces to build concrete entities)    
 |  |--validators (concrete validators)  
 |--infrastructure  
 |  |--cache (in memory caches and related functionality)  
 |  |--encryption (encrypts an input to produce hashed output)  
 |  |--filesystem (a fat gateway to work with an underlying file system)  
 |  |--storage (Json serialization / deserialization to store requests' metadata)
***Requests Examples***  
    - init /folder1/folder2  
    after completion creates miniGit folder under /folder1/folder2  
    - track /folder1/folfer2  
    mirrors changes in /folder1/folder2 under /miniGit/temp folder  
    - commit /folder1/folder2 "initial" (message is strictly required)  
    creates a "snapshot" of  /miniGit/temp under /miniGit/commits/shortHash/longhash/  
    - history /folder1/folder2  
    lists all existing commits for /folder1/folder2  
    - diff /folder1/folder2 34fer5 87grt1 (where 34fer5 87grt1 are short commit names)  
    demonstrates differences between two commits - added/changed/deleted directories / files  
    - undo /folder1/folder2  
    removes miniGit folder under /folder1/folder2, stops tracking repository  
    - restore /folder1/folder2 45tryoi23 (where 45tryoi23 is a short commit name)  
    rolls back the state of a working repository and its subfolders to a particular commit

    

