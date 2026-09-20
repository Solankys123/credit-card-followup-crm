const customers=[
{name:"Aarav Mehta",inquiry:"Long Inquiry",status:"Documents Pending",priority:"HIGH",next:"Call today 4:00 PM",source:"WhatsApp",category:"Gold",yono:"No",app:"DEMO-24001",pending:"Salary slip pending",phone:"98XXXXXX41",note:"Customer promised document today."},
{name:"Priya Nair",inquiry:"Short Inquiry",status:"Application Started",priority:"MEDIUM",next:"Follow up tomorrow 11:00 AM",source:"Direct",category:"Silver",yono:"Yes",app:"DEMO-24002",pending:"",phone:"97XXXXXX52",note:"Customer requested callback."},
{name:"Vikram Singh",inquiry:"Long Inquiry",status:"On Hold",priority:"HIGH",next:"Re-follow up in 7 days",source:"WhatsApp",category:"Silver",yono:"No",app:"DEMO-24003",pending:"Existing blocked card",phone:"96XXXXXX63",note:"Previous card reported blocked."},
{name:"Simran Kaur",inquiry:"Long Inquiry",status:"Documents Submitted",priority:"MEDIUM",next:"Check verification tomorrow",source:"Referral",category:"Gold",yono:"Yes",app:"DEMO-24004",pending:"",phone:"95XXXXXX74",note:"Documents received; verification pending."},
{name:"Rohan Gupta",inquiry:"Short Inquiry",status:"Not Interested",priority:"LOW",next:"Re-follow up after 30 days",source:"Direct",category:"Standard",yono:"Yes",app:"",pending:"",phone:"94XXXXXX85",note:"Customer asked to reconnect next month."},
{name:"Kavya Sharma",inquiry:"Long Inquiry",status:"Rejected",priority:"LOW",next:"Record loss reason",source:"WhatsApp",category:"Gold",yono:"No",app:"DEMO-24006",pending:"Very poor CIBIL",phone:"93XXXXXX96",note:"Synthetic QA case only."},
{name:"Aditya Joshi",inquiry:"Short Inquiry",status:"Approved",priority:"MEDIUM",next:"Track dispatch",source:"Referral",category:"Gold",yono:"Yes",app:"DEMO-24007",pending:"",phone:"92XXXXXX07",note:"Potential family add-on opportunity noted."}
];
const followups=[
["Aarav Mehta","Salary slip pending","Today, 4:00 PM","HIGH"],
["Priya Nair","Callback requested","Tomorrow, 11:00 AM","MEDIUM"],
["Vikram Singh","Existing blocked card clarification","27 Sep, 11:00 AM","HIGH"],
["Simran Kaur","Verification status","Tomorrow, 10:00 AM","MEDIUM"],
["Rohan Gupta","Customer requested later follow-up","18 Oct, 5:00 PM","LOW"]
];
let route="dashboard", selected=null, query="";

const el=()=>document.getElementById("app");
function shell(title,body,back){
el().innerHTML='<div class="shell"><header><div><div class="brand">Credit Card CRM</div><div class="sub">Browser Preview • Synthetic QA Data</div></div><div class="actions">'+(back?'<button class="secondary" onclick="go(\'dashboard\')">Back</button>':'')+'<button onclick="go(\'followups\')">Follow-ups</button></div></header>'+body+'</div>';
}
function stat(n,l){return '<div class="card metric"><b>'+n+'</b><span>'+l+'</span></div>'}
function badge(p){return '<span class="badge '+p.toLowerCase()+'">'+p+'</span>'}
function customerCard(c){return '<div class="card customer"><div class="customer-main"><div class="name">'+c.name+'</div><div class="muted">'+c.inquiry+' • '+c.status+'</div>'+badge(c.priority)+'<div class="small">Next: '+c.next+'</div></div><div class="actions"><button class="secondary" onclick="viewCustomer(\''+c.name+'\')">View</button></div></div>'}
function dashboard(){
shell("Dashboard",'<div class="grid">'+stat("2","Overdue")+stat("5","Follow-ups")+stat("3","Pending")+stat("7","Demo Customers")+'</div><div class="section"><h2>DO NOW</h2><div class="list">'+customers.slice(0,5).map(customerCard).join("")+'</div></div><div class="section actions"><button onclick="go(\'customers\')">Customers</button><button class="secondary" onclick="go(\'followups\')">Follow-ups</button></div>');
}
function customersPage(){
const q=query.toLowerCase();
const list=customers.filter(c=>[c.name,c.status,c.inquiry,c.app].join(" ").toLowerCase().includes(q));
shell("Customers",'<div class="section"><input placeholder="Search name, status, inquiry or application" value="'+query+'" oninput="query=this.value;customersPage()"></div><div class="section"><div class="list">'+list.map(customerCard).join("")+'</div></div>',true);
}
function detail(){
const c=selected;
shell(c.name,'<div class="row"><div class="card"><h2>'+c.name+'</h2><div class="muted">'+c.inquiry+' • '+c.category+'</div>'+badge(c.priority)+'<p><b>Status:</b> '+c.status+'</p><p><b>Application:</b> '+(c.app||"Not started")+'</p><p><b>Source:</b> '+c.source+'</p><p><b>YONO:</b> '+c.yono+'</p><p><b>Phone:</b> '+c.phone+'</p><div class="actions"><button>Call</button><button class="secondary" onclick="go(\'followups\')">Follow-up</button></div></div><div class="card"><h2>Documents & Issues</h2><p>✓ PAN</p><p>✓ Address Proof</p><p>☐ Income Proof</p><p><b>Pending/Issue:</b> '+(c.pending||"None")+'</p><p class="muted">'+c.note+'</p></div></div><div class="section card"><h2>Activity Timeline</h2><div class="timeline"><div><div class="k">20 Sep</div><div>Customer contacted</div></div><div><div class="k">20 Sep</div><div>Follow-up scheduled</div></div><div><div class="k">19 Sep</div><div>Application/process update</div></div></div></div>',true);
}
function followupsPage(){
shell("Follow-ups",'<div class="grid">'+stat("5","Today/Upcoming")+stat("2","High Priority")+stat("1","Overdue")+stat("0","Completed")+'</div><div class="section"><div class="list">'+followups.map(f=>'<div class="card customer"><div><div class="name">'+f[0]+'</div><div class="muted">'+f[1]+'</div><div class="small">Due: '+f[2]+' • '+f[3]+'</div></div><div class="actions"><button class="secondary" onclick="viewCustomer(\''+f[0]+'\')">Customer</button><button>Done</button></div></div>').join("")+'</div></div>',true);
}
function viewCustomer(name){selected=customers.find(c=>c.name===name);route="detail";render()}
function go(r){route=r; if(r==="dashboard")selected=null; render()}
function render(){if(route==="dashboard")dashboard();else if(route==="customers")customersPage();else if(route==="detail"&&selected)detail();else if(route==="followups")followupsPage();else dashboard()}
render();
