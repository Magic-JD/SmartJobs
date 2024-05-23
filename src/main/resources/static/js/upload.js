let files = new DataTransfer();
let names = [];
const dropContainer = document.getElementById("dropcontainer")
const fileInput = document.getElementById("files")
const clear = document.getElementById("clear")

clear.addEventListener('click', (e) => {
    files = new DataTransfer();
    fileInput.files = files.files
    names = []
})

fileInput.addEventListener('change', (e) => {
    newFiles = e.target.files
    Array.from(newFiles).forEach(f => {
        if(!names.includes(f.name)){
            names.push(f.name)
            files.items.add(f)
        }
    })
    fileInput.files = files.files
})

dropContainer.addEventListener("dragover", (e) => {
    // prevent default to allow drop
    e.preventDefault()
}, false)

dropContainer.addEventListener("dragenter", () => {
    dropContainer.classList.add("drag-active")
})

dropContainer.addEventListener("dragleave", () => {
    dropContainer.classList.remove("drag-active")
})

dropContainer.addEventListener("drop", (e) => {
    e.preventDefault()
    dropContainer.classList.remove("drag-active")
    newFiles = e.dataTransfer.files
    Array.from(newFiles).forEach(f => {
        if(!names.includes(f.name)){
            names.push(f.name)
            files.items.add(f)
        }
    })
    fileInput.files = files.files
})
