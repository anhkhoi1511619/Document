import { readFile, writeFile } from 'node:fs/promises';

let db = new Map();
let presets = new Set();
let preset = null;

async function init() {
    try {
        console.log("init new empty db");
        let json = (await readFile("./db.json")).toString();
        console.log(json);
        let dbEntries;
        let presetEntries;
        ({entries: dbEntries, preset, presets: presetEntries} = JSON.parse(json));
        //({})
        db = new Map(dbEntries);
        presets = new Set(presetEntries);
        console.log(db);
        console.log(preset);
        console.log(presets);
    } catch (error) {
        console.log("can't read existing db, init new empty db"+error);
        db = new Map();
        preset = "admin";
        presets.add(preset);
    }
}
init();
export function get(key) {
    console.log("[GET] database key: "+key);
    let info;
    try {
        if(!db) {
          console.log("DB is empty");
          return;
        }
        let info = db.get(`${preset}:${key}`);
        // console.log(info);
        return info;
    } catch (error) {
        console.log("can't get existing db error: "+error);
        init();
    }
    return info;
}

export async function set(key, obj) {
    console.log("[SET] database");

    if(!db) {
        console.log("DB is empty");
        return;
    }
    db.set(`${preset}:${key}`, obj);
    await save();
}

export async function save() {
    if(!db) {
        console.log("DB is empty");
        return;
    }
    let text = JSON.stringify({
        entries: Array.from(db.entries()),
        preset,
        presets: Array.from(presets.values()),
      });
    await writeFile("./static/db.json",text);
}

export async function getPreset() {
    return preset;
}

let token = "";

export async function getToken() {
    console.log("database token: "+token);
    return token;
}

export function generate_token(length){
    //edit the token allowed characters
    var a = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".split("");
    var b = [];  
    for (var i=0; i<length; i++) {
        var j = (Math.random() * (a.length-1)).toFixed(0);
        b[i] = a[j];
    }
    token = b.join("");
    console.log(token);
    return token;
}
