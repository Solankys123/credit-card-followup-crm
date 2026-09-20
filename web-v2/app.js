const TODAY=new Date("2026-09-20T12:00:00");
let C=[
 {n:"Aarav Mehta",i:"Long Inquiry",s:"Documents Pending",p:"HIGH",a:"Today, 4:00 PM",src:"WhatsApp",cat:"Gold",y:"No",app:"DEMO-24001",issue:"Salary slip pending",ph:"98XXXXXX41",note:"Customer promised document today.",days:0},
 {n:"Priya Nair",i:"Short Inquiry",s:"Application Started",p:"MEDIUM",a:"Tomorrow, 11:00 AM",src:"Direct",cat:"Silver",y:"Yes",app:"DEMO-24002",issue:"",ph:"97XXXXXX52",note:"Callback requested.",days:1},
 {n:"Vikram Singh",i:"Long Inquiry",s:"On Hold",p:"HIGH",a:"27 Sep, 11:00 AM",src:"WhatsApp",cat:"Silver",y:"No",app:"DEMO-24003",issue:"Existing blocked card",ph:"96XXXXXX63",note:"Previous card reported blocked.",days:7},
 {n:"Simran Kaur",i:"Long Inquiry",s:"Documents Submitted",p:"MEDIUM",a:"Tomorrow, 10:00 AM",src:"Referral",cat:"Gold",y:"Yes",app:"DEMO-24004",issue:"Verification pending",ph:"95XXXXXX74",note:"Documents received; verification pending.",days:1},
 {n:"Rohan Gupta",i:"Short Inquiry",s:"Not Interested",p:"LOW",a:"20 Oct, 11:00 AM",src:"Direct",cat:"Standard",y:"Yes",app:"",issue:"",ph:"94XXXXXX85",note:"Customer asked to reconnect next month.",days:30},
 {n:"Kavya Sharma",i:"Long Inquiry",s:"Rejected",p:"LOW",a:"Record loss reason",src:"WhatsApp",cat:"Gold",y:"No",app:"DEMO-24006",issue:"Very poor CIBIL",ph:"93XXXXXX96",note:"Synthetic QA case.",days:null},
 {n:"Aditya Joshi",i:"Short Inquiry",s:"Approved",p:"MEDIUM",a:"Add-on opportunity",src:"Referral",cat:"Gold",y:"Yes",app:"DEMO-24007",issue:"Family add-on opportunity",ph:"92XXXXXX07",note:"Synthetic QA case.",days:null}
];
const F=[
 {n:"Aarav Mehta",r:"Salary slip pending",t:"Today, 4:00 PM",p:"HIGH",type:"Today",age:0},
 {n:"Priya Nair",r:"Callback requested",t:"Tomorrow, 11:00 AM",p:"MEDIUM",type:"Upcoming",age:1},
 {n:"Vikram Singh",r:"Existing blocked card clarification",t:"27 Sep, 11:00 AM",p:"HIGH",type:"Overdue",age:-1},
 {n:"Simran Kaur",r:"Verification status",t:"Tomorrow, 10:00 AM",p:"MEDIUM",type:"Upcoming",age:1},
 {n:"Rohan Gupta",r:"Re-follow-up",t:"20 Oct, 11:00 AM",p:"LOW",type:"Upcoming",age:30}
];
let page="dash",sel=null,q="",priority="ALL";
function bd(p){return '<span class="badge '+p.toLowerCase()+'">'+p+'</span>'}
function shell(body,acts=""){document.getElementById("app").innerHTML='<div class="shell"><div class="top"><div><div class="brand">Credit Card CRM</div><div class="sub">Smart Follow-Up Workspace • Synthetic QA Data</div></div><div class="actions">'+acts+'</div></div>'+body+'</div>'}
function stat(n,l,k=""){return '<div class="metric"><b>'+n+'</b><span>'+l+'</span>'+(k?'<em>'+k+'</em>':'')+'</div>'}
function cc(c){return '<div class="card customer"><div class="customer-main"><div class="name">'+c.n+'</div><div class="muted">'+c.i+' • '+c.src+' • '+c.cat+'</div>'+bd(c.p)+'<span class="badge blue">'+c.s+'</span><div class="small">Next: '+c.a+'</div></div><div class="actions"><button class="alt" onclick="openC(\''+c.n+'\')">View</button><button class="alt" onclick="editCustomer(\''+c.n+'\')">Edit</button><button class="danger" onclick="deleteCustomer(\''+c.n+'\')">Delete</button><button onclick="page=\'follow\';render()">Follow-up</button></div></div>'}
function progress(label,val,total){let pct=total?Math.round(val/total*100):0;return '<div class="prog-row"><div class="prog-label"><span>'+label+'</span><b>'+val+'</b></div><div class="bar"><i style="width:'+pct+'%"></i></div></div>'}
function sameDay(a,b){return a.getFullYear()===b.getFullYear()&&a.getMonth()===b.getMonth()&&a.getDate()===b.getDate()}
function dash(){
 const now=TODAY,list=getFollowups(),open=list.filter(x=>x.status==="OPEN");
 const overdue=list.filter(x=>x.status==="OPEN"&&new Date(x.due)<now).length;
 const today=list.filter(x=>x.status==="OPEN"&&sameDay(new Date(x.due),now)).length;
 const upcoming=list.filter(x=>x.status==="OPEN"&&new Date(x.due)>now&&new Date(x.due)<=new Date(now.getTime()+7*86400000)).length;
 const pendingDocs=C.filter(c=>c.s.includes("Documents")).length;
 const pendingIssues=C.filter(c=>c.issue).length;
 const active=C.filter(c=>["Application Started","Documents Pending","Documents Submitted","On Hold","Verification"].includes(c.s)).length;
 const funnel=[["Inquiry",C.length],["Application",C.filter(c=>c.app).length],["Documents",C.filter(c=>c.s.includes("Documents")).length],["Approved",C.filter(c=>c.s==="Approved").length]];
 const issueBreak={"Documents":C.filter(c=>c.issue&&c.issue.toLowerCase().includes("document")).length,"CIBIL":C.filter(c=>c.issue&&c.issue.toLowerCase().includes("cibil")).length,"Existing card":C.filter(c=>c.issue&&c.issue.toLowerCase().includes("existing")).length,"Verification":C.filter(c=>c.issue&&c.issue.toLowerCase().includes("verification")).length};
 const pr=C.filter(c=>c.p==="HIGH"),pulse=open.slice().sort((a,b)=>new Date(a.due)-new Date(b.due)).slice(0,5);
 const pulseHtml=pulse.length?pulse.map(x=>'<div class="card pulse-item"><div><b>'+x.customer+'</b><span class="muted">'+x.reason+'</span></div><div class="right">'+bd(x.priority)+'<span>'+new Date(x.due).toLocaleString([], {dateStyle:"medium",timeStyle:"short"})+'</span></div></div>').join(""):'<div class="card pad muted">No open follow-ups. Create one from a customer or Follow-ups.</div>';
 shell('<div class="hero"><div><div class="eyebrow">GOOD AFTERNOON</div><h1>Today at a glance</h1><p>Live dashboard from your customer and follow-up data.</p></div><div class="hero-chip">'+C.length+' customers • '+active+' active applications</div></div><div class="grid stats">'+stat(overdue,"Overdue follow-ups","Needs attention")+stat(today,"Follow-ups today","Scheduled")+stat(upcoming,"Upcoming","Next 7 days")+stat(C.length,"Total customers","Live data")+stat(pendingDocs,"Pending documents","Customer blockers")+stat(pendingIssues,"Open issues","Needs review")+stat(active,"Active applications","In process")+stat(pr.length,"High priority","Do now")+'</div><div class="section"><div class="head"><div><h2>Priority work queue</h2><span class="muted">Customers most likely to need your action next</span></div><div class="filters"><button class="'+(priority==="ALL"?"active":"alt")+'" onclick="priority=\'ALL\';render()">All</button><button class="'+(priority==="HIGH"?"active":"alt")+'" onclick="priority=\'HIGH\';render()">High</button><button class="'+(priority==="MEDIUM"?"active":"alt")+'" onclick="priority=\'MEDIUM\';render()">Medium</button></div></div><div class="list">'+(priority==="ALL"?C.filter(c=>c.p!=="LOW").slice(0,4):C.filter(c=>c.p===priority).slice(0,4)).map(cc).join("")+'</div></div><div class="two-col"><div class="card panel"><div class="head"><div><h2>Application funnel</h2><span class="muted">Current pipeline</span></div></div>'+funnel.map(x=>progress(x[0],x[1],Math.max(1,C.length))).join("")+'</div><div class="card panel"><div class="head"><div><h2>Attention breakdown</h2><span class="muted">Open blockers by type</span></div></div>'+Object.entries(issueBreak).map(x=>progress(x[0],x[1],Math.max(1,pendingIssues))).join("")+'</div></div><div class="section"><div class="head"><div><h2>Follow-up pulse</h2><span class="muted">Live open follow-ups</span></div><button class="alt" onclick="page=\'follow\';render()">Open follow-ups</button></div><div class="pulse">'+pulseHtml+'</div></div><div class="two-col"><div class="card panel"><div class="head"><h2>Quick actions</h2></div><div class="quick"><button onclick="page=\'add\';render()">＋ Add Customer</button><button class="alt" onclick="page=\'cust\';render()">👥 Customers</button><button class="alt" onclick="page=\'follow\';render()">📞 Follow-ups</button><button class="alt" onclick="alert(\'Analytics module comes next.\')">📊 Analytics</button></div></div><div class="card panel"><div class="head"><h2>Recent activity</h2></div><div class="activity"><div><b>Live follow-up queue</b><span>'+open.length+' open follow-up(s)</span></div><div><b>Customer records</b><span>'+C.length+' customer(s) in CRM</span></div><div><b>Pending documents</b><span>'+pendingDocs+' customer(s)</span></div><div><b>Open issues</b><span>'+pendingIssues+' customer(s)</span></div></div></div></div>','<button class="active" onclick="page=\'dash\';render()">Dashboard</button><button class="alt" onclick="page=\'cust\';render()">Customers</button><button class="alt" onclick="page=\'follow\';render()">Follow-ups</button>')
}function cust(){let arr=C.filter(c=>(c.n+c.i+c.s+c.app+c.src+c.cat).toLowerCase().includes(q.toLowerCase()));shell('<div class="section"><input class="search" placeholder="Search name, application, status, source..." value="'+q+'" oninput="q=this.value;cust()"></div><div class="section"><div class="head"><h2>Customer Queue</h2><span class="muted">'+arr.length+' records</span></div><div class="list">'+arr.map(cc).join("")+'</div></div>','<button class="alt" onclick="page=\'dash\';render()">Dashboard</button><button onclick="page=\'follow\';render()">Follow-ups</button>')}
function customerFollowups(name){return getFollowups().filter(function(x){return x.customer===name}).sort(function(a,b){return new Date(b.due)-new Date(a.due)})}
function detail(){
 const c=sel, fl=customerFollowups(c.n);
 const followHtml=fl.length?fl.map(function(x){return '<div class="event"><b>'+x.reason+'</b><span class="small">'+x.due+' • '+x.priority+' • '+x.status+(x.status==="DONE"?" • Completed":"")+'</span></div>'}).join(""):'<div class="muted">No follow-up history yet.</div>';
 shell('<div class="row"><div class="card pad"><div class="head"><div><h2>'+c.n+'</h2><div class="muted">'+c.i+' • '+c.cat+' • '+c.src+'</div></div><button class="alt" onclick="editCustomer(\''+c.n+'\')">Edit</button></div>'+bd(c.p)+'<span class="badge blue">'+c.s+'</span><div class="section"><div class="kv"><b>Application</b><span>'+(c.app||"Not started")+'</span><b>Phone</b><span>'+c.ph+'</span><b>YONO</b><span>'+c.y+'</span><b>Next action</b><span>'+c.a+'</span><b>Issue</b><span>'+(c.issue||"None")+'</span></div></div><div class="section actions"><button>📞 Call</button><button onclick="newFollowupForCustomer()">📅 New Follow-up</button><button class="alt">💬 WhatsApp</button></div></div><div class="card pad"><h3>Documents</h3><p>✓ PAN</p><p>✓ Address Proof</p><p>'+(c.issue?'⏳ Income Proof — Pending':'✓ Income Proof')+'</p><h3>Customer Note</h3><p class="muted">'+c.note+'</p></div></div><div class="section card pad"><div class="head"><div><h2>Follow-up History</h2><span class="muted">'+fl.length+' follow-up(s)</span></div><button class="alt" onclick="newFollowupForCustomer()">＋ Add Follow-up</button></div><div class="timeline">'+followHtml+'</div></div><div class="section card pad"><div class="head"><h2>Activity Timeline</h2><button class="alt" onclick="addCustomerNote()">＋ Add Note</button></div><div class="timeline"><div class="event"><b>Customer record</b><span class="small">Customer is available in CRM.</span></div><div class="event"><b>Application</b><span class="small">'+(c.app||"No application number yet")+' • '+c.s+'</span></div></div></div>','<button class="alt" onclick="page=\'cust\';render()">← Customers</button><button onclick="page=\'follow\';render()">Follow-ups</button>');
}
function newFollowupForCustomer(){
 const options='<option selected>'+sel.n+'</option>';
 shell('<div class="section"><div class="card pad"><h2>New Follow-up — '+sel.n+'</h2><form onsubmit="saveCustomerFollowup(event)"><div class="form-grid"><label>Customer<select id="cf_customer">'+options+'</select></label><label>Date<input id="cf_date" type="date" required></label><label>Time<input id="cf_time" type="time" required></label><label>Reason<input id="cf_reason" required placeholder="e.g. Call customer"></label><label>Priority<select id="cf_priority"><option>HIGH</option><option>MEDIUM</option><option>LOW</option></select></label></div><div class="actions"><button type="submit">Create Follow-up</button><button type="button" class="alt" onclick="page=\'detail\';render()">Cancel</button></div></form></div></div>','<button class="alt" onclick="page=\'detail\';render()">Customer</button>');
}
function saveCustomerFollowup(e){
 e.preventDefault();
 const item={id:"FU-"+Date.now(),customer:sel.n,due:document.getElementById("cf_date").value+"T"+document.getElementById("cf_time").value,reason:document.getElementById("cf_reason").value.trim(),priority:document.getElementById("cf_priority").value,status:"OPEN",created:new Date().toISOString()};
 const list=getFollowups();list.unshift(item);saveFollowups(list);page="detail";render();
}
function addCustomerNote(){
 const note=prompt("Add customer note");
 if(!note)return;
 sel.note=sel.note?sel.note+" | "+note:note;
 localStorage.setItem("crm_customers",JSON.stringify(C));render();
}
function loadCustomers(){try{const saved=JSON.parse(localStorage.getItem("crm_customers")||"null");if(Array.isArray(saved))C=saved.concat([])}catch(e){}}
const SEED_FOLLOWUPS=[
 {id:"DEMO-FU-1",customer:"Aarav Mehta",due:"2026-09-20T16:00",reason:"Salary slip pending",priority:"HIGH",status:"OPEN",created:"2026-09-20T09:00:00"},
 {id:"DEMO-FU-2",customer:"Priya Nair",due:"2026-09-21T11:00",reason:"Callback requested",priority:"MEDIUM",status:"OPEN",created:"2026-09-20T09:00:00"},
 {id:"DEMO-FU-3",customer:"Vikram Singh",due:"2026-09-19T11:00",reason:"Existing blocked card clarification",priority:"HIGH",status:"OPEN",created:"2026-09-19T09:00:00"},
 {id:"DEMO-FU-4",customer:"Simran Kaur",due:"2026-09-21T10:00",reason:"Verification status",priority:"MEDIUM",status:"OPEN",created:"2026-09-20T09:00:00"},
 {id:"DEMO-FU-5",customer:"Rohan Gupta",due:"2026-10-20T11:00",reason:"Re-follow-up",priority:"LOW",status:"OPEN",created:"2026-09-20T09:00:00"}
];
function getFollowups(){try{const raw=localStorage.getItem("crm_followups");if(raw===null){localStorage.setItem("crm_followups",JSON.stringify(SEED_FOLLOWUPS));return SEED_FOLLOWUPS.slice()}const parsed=JSON.parse(raw);return Array.isArray(parsed)?parsed:[]}catch(e){return[]}}
function saveFollowups(list){localStorage.setItem("crm_followups",JSON.stringify(list))}
function follow(){
 const list=getFollowups();
 const now=new Date("2026-09-20T12:00:00");
 const counts={active:list.filter(x=>x.status==="OPEN").length,high:list.filter(x=>x.priority==="HIGH"&&x.status==="OPEN").length,over:list.filter(x=>x.status==="OPEN"&&new Date(x.due)<now).length,done:list.filter(x=>x.status==="DONE").length};
 shell('<div class="grid stats">'+stat(counts.active,"Active follow-ups")+stat(counts.high,"High priority")+stat(counts.over,"Overdue")+stat(counts.done,"Completed")+'</div><div class="section"><div class="head"><div><h2>Follow-up Queue</h2><span class="muted">Create, complete or reschedule customer follow-ups</span></div><button onclick="newFollowup()">＋ New Follow-up</button></div><div class="filters"><button class="alt" onclick="followFilter=&quot;ALL&quot;;render()">All</button><button class="alt" onclick="followFilter=&quot;TODAY&quot;;render()">Today</button><button class="alt" onclick="followFilter=&quot;OVERDUE&quot;;render()">Overdue</button><button class="alt" onclick="followFilter=&quot;UPCOMING&quot;;render()">Upcoming</button><button class="alt" onclick="followFilter=&quot;DONE&quot;;render()">History</button></div><div class="list section">'+list.filter(function(x){if(followFilter==="DONE")return x.status==="DONE";if(x.status==="DONE")return false;if(followFilter==="OVERDUE")return new Date(x.due)<now;if(followFilter==="TODAY")return new Date(x.due).toDateString()===now.toDateString();if(followFilter==="UPCOMING")return new Date(x.due)>now;return true}).map(function(x){return '<div class="card customer"><div><div class="name">'+x.customer+'</div><div class="muted">'+x.reason+'</div>'+bd(x.priority)+'<div class="small">Due: '+x.due+' • '+x.status+'</div></div><div class="actions">'+(x.status==="OPEN"?'<button onclick="completeFollowup(\''+x.id+'\')">✓ Done</button><button class="alt" onclick="rescheduleFollowup(\''+x.id+'\')">Reschedule</button>':'<span class="badge low">Completed</span>')+'</div></div>'}).join("")+'</div></div>','<button class="alt" onclick="page=&quot;dash&quot;;render()">Dashboard</button><button class="alt" onclick="page=&quot;cust&quot;;render()">Customers</button>');
}
let followFilter="ALL";
function newFollowup(){
 const options=C.map(function(c){return '<option value="'+c.n+'">'+c.n+'</option>'}).join("");
 shell('<div class="section"><div class="card pad"><h2>New Follow-up</h2><form onsubmit="saveNewFollowup(event)"><div class="form-grid"><label>Customer<select id="fu_customer">'+options+'</select></label><label>Date<input id="fu_date" type="date" required></label><label>Time<input id="fu_time" type="time" required></label><label>Reason<input id="fu_reason" required placeholder="e.g. Document pending"></label><label>Priority<select id="fu_priority"><option>HIGH</option><option>MEDIUM</option><option>LOW</option></select></label></div><div class="actions"><button type="submit">Create Follow-up</button><button type="button" class="alt" onclick="page=&quot;follow&quot;;render()">Cancel</button></div></form></div></div>','<button class="alt" onclick="page=&quot;follow&quot;;render()">Follow-ups</button>');
}
function saveNewFollowup(e){
 e.preventDefault();
 const c=document.getElementById("fu_customer").value;
 const due=document.getElementById("fu_date").value+"T"+document.getElementById("fu_time").value;
 const item={id:"FU-"+Date.now(),customer:c,due:due,reason:document.getElementById("fu_reason").value.trim(),priority:document.getElementById("fu_priority").value,status:"OPEN",created:new Date().toISOString()};
 const list=getFollowups();list.unshift(item);saveFollowups(list);page="follow";render();
}
function completeFollowup(id){
 const list=getFollowups();const x=list.find(function(v){return v.id===id});if(!x)return;
 x.status="DONE";x.completedAt=new Date().toISOString();saveFollowups(list);render();
}
function rescheduleFollowup(id){
 const list=getFollowups();const x=list.find(function(v){return v.id===id});if(!x)return;
 const date=prompt("New date (YYYY-MM-DD)",x.due.slice(0,10));if(!date)return;
 const time=prompt("New time (HH:MM)",x.due.slice(11,16));if(!time)return;
 x.due=date+"T"+time;x.status="OPEN";x.rescheduledAt=new Date().toISOString();saveFollowups(list);render();
}
function openC(n){sel=C.find(x=>x.n===n);page="detail";render()}function render(){loadCustomers();if(page==="dash")dash();else if(page==="cust")cust();else if(page==="add"){window.CrmAddCustomer.open()}else if(page==="detail"&&sel)detail();else follow()}render();
function editCustomer(name){
 const c=C.find(function(x){return x.n===name});
 if(!c)return;
 const esc=function(v){return String(v==null?"":v).replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;")};
 shell('<div class="section"><div class="card pad"><div class="head"><div><h2>Edit Customer</h2><span class="muted">'+esc(c.n)+'</span></div><button class="alt" onclick="page=\'cust\';render()">Cancel</button></div><form id="editCustomerForm"><div class="form-grid"><label>Full name<input id="ec_name" value="'+esc(c.n)+'" required></label><label>Phone<input id="ec_ph" value="'+esc(c.ph)+'" required></label><label>Email<input id="ec_email" value="'+esc(c.email)+'"></label><label>PAN<input id="ec_pan" value="'+esc(c.pan)+'"></label><label>Date of Birth<input id="ec_dob" type="date" value="'+esc(c.dob)+'"></label><label>Mother's name<input id="ec_mother" value="'+esc(c.mother)+'"></label><label>Current address<input id="ec_address" value="'+esc(c.address)+'"></label><label>Office name<input id="ec_office" value="'+esc(c.office)+'"></label><label>Office address<input id="ec_officeAddress" value="'+esc(c.officeAddress)+'"></label><label>Category / Card<input id="ec_cat" value="'+esc(c.cat)+'"></label><label>Inquiry type<select id="ec_i"><option>Short Inquiry</option><option>Long Inquiry</option></select></label><label>Source<select id="ec_src"><option>WhatsApp</option><option>Direct</option><option>Referral</option><option>Existing</option><option>Other</option></select></label><label>YONO available<select id="ec_y"><option>Yes</option><option>No</option></select></label><label>Application / reference no.<input id="ec_app" value="'+esc(c.app)+'"></label><label>Application status<select id="ec_s"><option>New Inquiry</option><option>Application Started</option><option>Documents Pending</option><option>Documents Submitted</option><option>Verification</option><option>Approved</option><option>On Hold</option><option>Rejected</option><option>Not Interested</option></select></label><label>Pending reason<input id="ec_pending" value="'+esc(c.pending)+'"></label><label>Priority<select id="ec_p"><option>LOW</option><option>MEDIUM</option><option>HIGH</option></select></label><label>Next action<input id="ec_a" value="'+esc(c.a)+'"></label><label>Issue<input id="ec_issue" value="'+esc(c.issue)+'"></label><label>Customer notes<input id="ec_note" value="'+esc(c.note)+'"></label></div><div class="actions"><button type="submit">Save Changes</button><button type="button" class="alt" onclick="page=\'cust\';render()">Cancel</button></div></form></div></div>','<button class="alt" onclick="page=\'cust\';render()">← Customers</button>');
 const set=function(id,value){const e=document.getElementById(id);if(e)e.value=value==null?"":value};
 set("ec_i",c.i);set("ec_src",c.src);set("ec_y",c.y);set("ec_s",c.s);set("ec_p",c.p);
 document.getElementById("editCustomerForm").addEventListener("submit",function(ev){
   ev.preventDefault();
   const v=function(id){return document.getElementById(id).value.trim()};
   const updated={n:v("ec_name"),ph:v("ec_ph"),email:v("ec_email"),pan:v("ec_pan"),dob:v("ec_dob"),mother:v("ec_mother"),address:v("ec_address"),office:v("ec_office"),officeAddress:v("ec_officeAddress"),cat:v("ec_cat"),i:v("ec_i"),src:v("ec_src"),y:v("ec_y"),app:v("ec_app"),s:v("ec_s"),pending:v("ec_pending"),p:v("ec_p"),a:v("ec_a"),issue:v("ec_issue"),note:v("ec_note"),days:c.days};
   const index=C.findIndex(function(x){return x.n===name});
   if(index<0)return;
   C[index]=updated;
   localStorage.setItem("crm_customers",JSON.stringify(C));
   sel=updated;
   alert("Customer updated successfully");
   page="cust";
   render();
 });
}
function deleteCustomer(name){
 const c=C.find(function(x){return x.n===name});
 if(!c)return;
 if(!window.confirm("Delete customer \""+name+"\"? This will remove the customer from this browser."))return;
 C=C.filter(function(x){return x.n!==name});
 localStorage.setItem("crm_customers",JSON.stringify(C));
 const followups=getFollowups().filter(function(x){return x.customer!==name});
 saveFollowups(followups);
 if(sel&&sel.n===name)sel=null;
 alert("Customer deleted successfully");
 page="cust";
 render();
}
